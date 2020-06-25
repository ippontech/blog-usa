---
authors:
- Lane Jennison
tags:
- Github Actions
- CI/CD
- Secrets Management
- monorepo
- Single Board Computers
- Linux
date: 2020-06-23T12:21:50.000Z
title: "CI Test unusual projects with GitHub actions and self-hosted runners"
image:
---
I contribute to [Armbian](https://armbian.com), a Linux distribution for 120+ bespoke ARM-based Single Board Computers (SBCs) such as the [Odroid XU4](https://wiki.odroid.com/odroid-xu4/odroid-xu4), [Libre Computer Le Potato](https://libre.computer/products/boards/aml-s905x-cc/) and the [Orange Pi 3](http://www.orangepi.org/Orange%20Pi%203/). 

Early this year, we introduced rudimentary CI testing with some basic integration between Jenkins and Github. Recently we chose to switch to our CI to Github Actions.

# The Scope of Testing is a Challenge

Unlike x86 architecture, there are fewer standards for booting and system configuration SBCs.   The [Armbian Build System](https://github.com/armbian/build) has to manage different configurations for dozens of System on Chip (SoC) architectures, sub-architectures, as well as config for the SBCs themselves.

We need to be able to build and test against those different configurations as contributions are submitted.

# Why Switch to Github Actions

We were running Jenkins off of my home internet connection.  Occasionally it would go off-line, crash, or I would need to do other system maintance.  Project maintainers are all over the world, so we needed to improve our uptime.

The integration plugins for Github were _okay_, but not intuitive.  Given that Github actions are native to Github, we hoped integration woud be simpler.

Runner configuration for Jenkins quite antiquated. The defacto method is still SSH-based, where the controller connects to client.  This makes it difficult to distribute runners as SSH access is required.  A beta websockets version of the Jenkins client has been recently introduced, but we didn't test it.

# Add Custom Self-Hosted runner to Project
## Why Self-Hosted?
The CI testing needs for Armbian are a bit unique.  Since it compiles linux kernels and cross-compiles debian packages, we have a need for large compute resources.  The more cores the better. 

The project doesn't have much sponsorship, so 40 core VMs in the cloud would not be a cost effective solution.  Instead we leverage _on-prem_ compute resources of the project maintainers' personal workstations and servers.

Caching is critical as the toolchains alone are 14G in size. When building full system images, we need to cache filesystems, kernel packages and more.  This makes ephemeral instances impractical without complex volume management to keep tools and caches current and available.

The image build process also requires privileges system access.  Filesytems are created by mounting loopback images in a chroot environment. 

A test on a clean uncached 4-core EC2 runner takes over an hour to run, whereas a test on a properly cached 40-core runner takes less than 10 minutes. 

## Configuring the Runner

The runners consist of an Ubuntu VM with a few basic dependencies and the Github Runner installed.

I found a very helpful [role on ansible galaxy](https://galaxy.ansible.com/monolithprojects/github_actions_runner) that handles the runner installation, queries the github API for access and attaches to the git repo.

I made an ansible playbook to install docker, create a privileged user account for the github runner, and apply the github actions runner ansible role.


```yaml
## prereq: ansible-galaxy install monolithprojects.github_actions_runner

- hosts: [ 'github_runners' ]
  ignore_errors: true 

  vars:
    - github_account: example
    - github_repo: example
    - access_token: "{{ vaulted_example }}"
    - runner_user: github
  
  pre_tasks:
   - name: install python
     raw: "apt install python3 -y"
     become: yes

   - name: get facts
     setup:

  tasks:

# Ubuntu 18.04 provides git 1.17
# https://github.com/actions/checkout requires git >= 1.18
  - name: install git >= 1.18
    apt_repository:
      repo: ppa:git-core/ppa

  - name: install packags
    apt:
     name: "{{ item }}"
     update_cache: yes
     state: latest
    loop:
      - docker.io
      - git

  - name: add privileged user account for github runner
    user:
      name: "{{ runner_user }}"
      shell: /bin/bash
      groups: sudo, docker
      append: yes

  - name: configure passwordless sudo
    lineinfile:
      dest: /etc/sudoers
      line: "%sudo  ALL=(ALL) NOPASSWD: ALL"
      regex: "^%sudo"

  - name: github actions runner role
    include_role:
      name: monolithprojects.github_actions_runner

```
The new runner joined the repo after playbook execution.
`ansible-playbook github-runner.yml --become`

![](images/2020/06/github-actions-self-hosted-runners-1.png)




# Implement CI-Testing
Fortunately our Jenkins CI-testing was primarily a [library of bash functions](https://github.com/armbian/ci-testing-tools). This made migrating fairly simple.

We created an action workflow consisting of 2 jobs, shellcheck and and compiling a changed kernel.

Notice the shellcheck job does not have a `self-hosted` label on its runs-on parameter.  Since shellcheck is just a linter, we were able to use runners provided by Github. 

There is a [shellcheck github action](https://github.com/marketplace/actions/shellcheck), but it was difficult to limit testing to the file globs we wanted to target.  Manually running shellcheck proved to be simpler and more flexible.

The second job uses our [CI library](https://github.com/armbian/ci-testing-tools) to test building an SBC kernel based on the files changes in the commit. This job runs on our self-hosted runners.

```yaml
name: test pull request
# This workflow is triggered on pushes to the repository.
on: [pull_request]

jobs:
  shellcheck:
    name: shellcheck
    runs-on: ubuntu-latest
    steps:
       - name: Checkout repository
         uses: actions/checkout@v2
       - name: Environment variables
         run: sudo -E bash -c set
       - name: Install required packages
         run: sudo apt-get install shellcheck
       - name: "lint libraries via shellcheck"
         run: shellcheck --color -x -s bash lib/*.sh && echo "shellcheck lib/*.sh - OK";
  build_sbc_kernel:
    name: Compile changed kernel
    # This job runs on self hosted Linux machine, with public label
    runs-on: [self-hosted, public]
    steps:
      - name: fix sudo ownership
        run: |
          stat build && sudo chown -R $(whoami) build || echo "fresh workspace detected"
      - uses: actions/checkout@v2
        with:
          fetch-depth: 0
          path: build
          clean: false
      - uses: actions/checkout@v2
        with:
          repository: armbian/ci-testing-tools
          path: ci-testing-tools
      - name: Find SBC target and build kernel
        shell: bash {0}         
        run: |  
         cd build
         GIT_COMMIT=$(echo $GITHUB_SHA)
         GIT_PREVIOUS_COMMIT=$(git rev-parse origin/$GITHUB_BASE_REF)
         ARMBIAN_BOARD=tritium-h5
         ARMBIAN_BRANCH=current
         cd ..
         env
         source ci-testing-tools/jenkins_ci.sh
         
         mkdir -p build/userpatches
         cp -f ci-testing-tools/config-jenkins-kernel.conf build/userpatches/
         configure_monorepo_watcher
         generate_board_table
         load_board_table
         cd build
         get_files_changed
         get_build_target
         git checkout ${GITHUB_SHA}
         git branch -v
         export GPG_TTY=$(tty)
         build_kernel jenkins-kernel
```

## Determining Test Parameters in a Monolithic Codebase

Since configurations for all SBCs and SoC families reside in the same repo, we approach testing as a monorepo. The test target is dynamically determined based on what files have changed.

I adapted the code from [slimm609's monorepo-gitwatcher](https://github.com/slimm609/monorepo-gitwatcher) into shell functions that identify files changed that coorespond to SBCs and SoCs.  The list of changed files is captured into vars for later processing.

```bash
detect_git_changes() {
  local watch_files=${1}
  
  oldIFS=${IFS}
  IFS=$'\r\n' GLOBIGNORE='*' command eval 'IGNORE_FILES=($(cat $watch_files))'
  IFS=${oldIFS}
  folders=$(git diff --name-only ${GIT_COMMIT} ${GIT_PREVIOUS_COMMIT} | sort -u | uniq)
  changed_components=${folders}

  for component in ${changed_components}; do
    for file in ${IGNORE_FILES[@]}; do
      if echo ${component} | grep -q ${file}; then
        echo "${component} has changed"
      fi
    done
  done

}

get_files_changed() {
  ## these var values needed by detect_git_changes
  echo "GIT_COMMIT=${GIT_COMMIT}"
  echo "GIT_PREVIOUS_COMMIT=${GIT_PREVIOUS_COMMIT}"
  
  family_changed="$(detect_git_changes ../family.watch)"
  board_changed="$(detect_git_changes ../board.watch)"
  
}
```

The list of changed files is scored against a dynamically generated list of build targets.  If there are no matches, a default value is used.

```bash
get_build_target() {
  current_score=0
  board_score=0
  IFS=$'\n'
  for row in ${BOARD_TABLE[@]}; do
    _info "board row ${row[@]}" 
    family=$(echo $row|cut -d',' -f1)
    board=$(echo $row|cut -d',' -f4)
    for family_row in ${family_changed}; do
      current_score=$(echo $family_row | fgrep -o -e ${family} -e $(translate_family ${family})|wc -c)
      _info "score: ${current_score} | family_row: ${family_row}"
      if [[ $current_score -gt $board_score ]]; then
         board_score=$current_score
         ARMBIAN_BOARD=${board}
         _info "ARMBIAN_BOARD=${board}"
         for branch in current dev legacy; do
           if echo $family_row |fgrep -q $branch; then
              _info "ARMBIAN_BRANCH=${branch}"
              ARMBIAN_BRANCH=${branch}
           fi
         done
      fi
    done
    for board_row in ${board_changed}; do  
      current_score=$(echo $board_row | fgrep -o ${board}|wc -c)
      _info "score: ${current_score} | board_row: ${board_row}"
      if [[ $current_score -gt $board_score ]]; then
        ARMBIAN_BOARD=${board}
        _info "ARMBIAN_BOARD=${board}"
      fi
    done
  
  done
  _info "${ARMBIAN_BOARD} ${ARMBIAN_BRANCH} selected"
}

```

Once the target is identified, the build is executed.

```bash
build_kernel() {
  local build_config=${1}
  ./compile.sh ${build_config} BOARD=${ARMBIAN_BOARD} BRANCH=${ARMBIAN_BRANCH}
}
```

# Managing Secrets

Our next phase of CI/CD Jobs involves GPG signing packages and uploading artifacts to our mirrors via rsync-over-ssh. 

Github's access control around secrets isn't very robust. Any user with commit access to master branch can retrieve Github secrets.  This made storing our private keys directly in Github secrets unappealing.

Since we consider our runners to be untrusted hosts, having the secrets stored on the filesystem was also unappealing.

The compromise was to encrypt the secrets with ccrypt and store the encrypted filese on runners selected as designated hosts for uploading and signing. The decryption keyword is stored in Github secrets. A little bash scripting magic lets us only store the deycrypted values in memory, and never on disk--or on Github.

`eval $(ssh-agent)` causes the script to launch ssh-agent and automatically export the environment variables ssh-agent outputs during runtime.  These values are needed for an ssh client to use the agent.

`ssh-add <(ccat $PWD/${SSH_KEYFILE} -K ${{secrets.keyword}})` decrypts the ssh private key into memory using the github secret `keyword`.

The `${{secret.variable}}` causes Github actions (not bash) to perform a substitution during run time.  The secret is automatically obfuscated by Github actions. 

The `<(command)` redirects the output of a command into a file descriptor so a command can read it as a file stream, despite only being in memory.  Additionally ssh-agent only stores private keys in memory.

Rsync is uploads the testfile via ssh because of the `user@host:` syntax. The ssh-private key is automatically used from ssh agent.

```yaml
jobs:
  test_secrets_management:
    name: do secrety things
    runs-on: [self-hosted]
    steps:
      - uses: actions/checkout@v2
        with:
          fetch-depth: 0
          path: ci-test
          clean: true
      - name: Do some tests
        shell: bash {0}
        run: |
         eval $(ssh-agent)
         cd ci-test
         TEST_HOST=testhost.example.com
         TEST_USER=rsync_user
         SSH_KEYFILE="secrets/test_ssh_key.cpt"
         ssh-add <(ccat $PWD/${SSH_KEYFILE} -K ${{secrets.keyword}})
         echo "Unique Data $(uuidgen)" > testfile
         rsync -auv testfile ${TEST_USER}@${TEST_HOST}:
```

# Observations and Challenges


## A workflow must exist before a worker can be added

The Github Runner ansible role was unable to a runner to a repo that had no actions configured. First create a simple github actions workflow and commit to repo.  Once a basic workflow exist, a runner can be added.

## Git >= 1.18 required

The [Github Checkout Action](https://github.com/actions/checkout) requires the runner to have Git v1.18 or greater, otherwise it falls back to using the Github rest about to download a tar of a repo. This can be impactful, if your CI testing depends on reading information from an actual git repo.  Our runners use Ubuntu 18.04 LTS, which comes with Git 1.17.  We install a newer git from a PPA archive during runner provisioning as a workaround.

## Documentation has Gaps

I was unable to find straightforward information about the filesystem topology and the workspace environment.  My checkouts landed in weird paths with rendunant names such as `/full/path/to/workspace/repo_name/repo_name`

## Access Control

Access control does not seem very robust. Large organizations may want to stick with older more-proven platforms such as Jenkins.

# Future Plans

It's been great to get quick feedback on Pull Requests without performing manual testing.  The improved upime with GitHub actions has also been beneficial to giving our CI testing credibility.

We look forward to enhancing our CI system, with more distrubted image builds, testing running images against actual SBC hardware, and creating dashboards to track the trends of our challenges and successes.
