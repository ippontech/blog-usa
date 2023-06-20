---
authors:
- John Blat
tags:
- Linux
- eBPF
- Kernel Modules
date: 2023-06-16T12:48:33.000Z
title: "eBPF and Kernel Modules - What's the Difference?"
image:
---

Extended Berkeley Packet Filter (eBPF) programs and kernel modules are both programs that can be dynamically loaded into the Linux kernel to extend or modify kernel functionality. Understanding the differences between the two will help in picking the appropriate method for a given task. This is helpful if you need to create your own solution that needs to be implemented in kernel-space, or if you are just interested in delving into kernel programming.

# Scope and Use-Cases:

eBPF’s scope is primarily intended for Security, Observability, and Networking. It can be used to write tools that can overwrite parts of the Linux networking stack, make decisions about a network packet before it even reaches the kernel, be used for observing kernel system calls, and much more.

Kernel Modules are much broader in scope than eBPF. They can do almost anything an eBPF program can do and more. Linux kernel modules have direct hardware access that can be used to create programs such as device drivers. Kernel modules can also be used to create filesystems.

Some network interface cards (NIC) support running an eBPF program type called [XDP](https://www.tigera.io/learn/guides/ebpf/ebpf-xdp/) that can process packets as soon as the NIC receives the packet and before the packet reaches the kernel. To do this with a linux kernel module, the NIC vendor would need to provide an SDK and would require the programmer to have a low-level knowledge of the card.

With all this being said, the scope of eBPF might expand in the future. In the book _Learning eBPF_, the author says that work is currently being done to add hardware device support to eBPF.

# Runtime

The following is a simplified explanation.

When kernel modules are loaded into the kernel, the kernel allocates memory for that program to execute, gives it all the privileges kernel code would get, and keeps a list tracking all kernel module names and locations so that the kernel can initialize and cleanup modules and users can manage them. Kernel module code runs directly on the CPU, like the kernel does.

eBPF programs on the other hand will run as bytecode inside the eBPF VM, which is a subsystem of the linux kernel (much like Java bytecode on the JVM). The bytecode is Just-in-Time (JIT) compiled into machine code during execution. The eBPF Linux subsystem will also keep track of running programs.

# Examples of Existing Applications

- eBPF programs:
	- [Katran](https://github.com/facebookincubator/katran), a load balancing  forwarding plane used by facebook that has very fast speeds for processing packets.
	- [Cilium](https://cilium.io/get-started/) is a networking and security project that provides a CNI (Container Network Interface) for Kubernetes. It does load balancing, network policy enforcement, and network visibility on a logical service/application level (as opposed to an IP Address level).
	- [Suricata](https://suricata.io/) is a high performance Network Intrusion Detection System, Intrusion Prevention System, and Security monitoring engine.
	- [Falco](https://falco.org/) (eBPF driver) is a tool for runtime security and visibility.

- Kernel Modules:
	- Iptables and nftables which can configure the built-in linux firewall
	- [OpenZFS](https://openzfs.org/wiki/Main_Page), an advanced filesystem for linux
	- [Wireguard](https://www.wireguard.com/), a VPN protocol with a focus on simplicity
	- NVIDIA/AMD graphics drivers
	- [VirtualBox](https://www.virtualbox.org/), an x86 hardware virtualizer which has host modules that are loaded into the kernel.
	- Falco (kernel module driver)
	- Various device drivers for mice, printers, and other hardware

# Installing Applications

When installing existing eBPF or Kernel Module programs, there's a good chance it will be installed just as any other Linux program is: via a package manager, a distributed binary, or an install script.

Cilium ships a client CLI binary that is used to install the Cilium eBPF components onto kubernetes clusters. Falco is shipped as a package for both the eBPF and kernel module drivers. Wireguard is shipped as a package. Some kernel modules are shipped in Linux distributions and then can be enabled on install.


Installing from source is a different story.

In eBPF's case, eBPF development packages are needed for compilation. Once the eBPF program is compiled, it will most likely be loaded into the eBPF kernel subsystem with the help of a user-space application that loads and manages the program. The user-space code will also be shipped with the eBPF code and will also require compiling or having that language's interpreter.

In Kernel Module's case, kernel module development packages are needed for compilation. The build system is typically written in a Makefile. Once the binary is compiled, it will need to be loaded with kernel module management command `insmod`.


# Supported Programming Languages
eBPF programs can be written in any language that has a compiler that can output eBPF bytecode. Currently, and as far as I know, those languages are C, Rust, [BPFTrace](https://github.com/iovisor/bpftrace) and [P4](https://github.com/p4lang/p4c/tree/main).

Kernel modules can be written in C or Rust.

As mentioned, eBPF programs are usually launched with user-space applications. These applications can be written in C, Rust, Go, Python, or any language that has libraries that can load eBPF binaries into the eBPF linux subsystem.

# Development Rigidity
Kernel Modules are very general purpose and can be used to write any type of program. There is also no hand-holding or deep rigidity outside of needing to provide initialization and cleanup functions.

eBPF on the other hand is very rigid and structured. There are even different specified eBPF program types. Each program type will then be restricted on the types of data or functions they are allowed to access since each program is geared towards a narrow domain. To get an eBPF program to compile, it must also pass a verifier check for program correctness and complexity.

# Program Bugs and Safety
- Kernel modules
	- Some bugs may have the chance of crashing the entire system. For example, [the infamous NULL pointer dereference](https://www.suse.com/support/kb/doc/?id=000021042)
	- [Lack of tighter security boundaries can risk corrupting the kernel or be more vulnerable to exploits](https://ebpf.io/what-is-ebpf/#ebpfs-impact-on-the-linux-kernel)

- eBPF
	- The eBPF verifier makes eBPF programming more rigid, but also will attempt to improper memory access, infinite loops, code-complexity issues such as too large of a program stack (512 byte limit), and everything in between.
	- Note: Although one goal of eBPF is to be designed with security in mind and to avoid pitfalls of oversight that can occur in kernel module development, security issues have happened ([CVE-2017-16995](https://bugzilla.redhat.com/show_bug.cgi?id=CVE-2017-16995),[CVE-2020-8835] (https://www.zerodayinitiative.com/blog/2020/4/8/cve-2020-8835-linux-kernel-privilege-escalation-via-improper-ebpf-program-verification)) and will likely happen in the future

If you don't know what you're doing when writing a kernel module, odds are you can completely crash your system. Conversely, you _need to know what you're doing_ to get an eBPF program to do any damage.


# Portability Across Kernel Versions
- Kernel modules
	- There is a chance that the kernel module will become obsolete or restricted to lower kernel versions if updates are not made. The kernel module will require updates alongside kernel updates if the kernel has a change that the kernel module is incompatible with. This is because kernel modules are dependent on the internal data structures of the kernel. For example, take a look at [this sample kernel module program](https://sysprog21.github.io/lkmpg/#system-calls) and notice the many `if #define`s for different kernel versions to achieve the same functionality on different kernel versions.  

- eBPF
	- eBPF programs that are compiled with an eBPF developer library that support [Compile-Once Run-Everywhere (CO-RE)](https://facebookmicrosites.github.io/bpf/blog/2020/02/19/bpf-portability-and-co-re.html), will be able to continue to work on kernel updates even if kernel data structures or other aspects of the kernel change. This is because of abstraction layers and the design of eBPF libraries take when building the eBPF program as well as abstraction layers present in the run-time.
	- One thing to note is that there are eBPF development libraries that do not take advantage of CO-RE. We will look at such a library later since those libraries provide a simpler means of getting started.
	- Production projects will be more incentivized to write eBPF programs using the more difficult, but more portable CO-RE libraries.

A takeaway from this is that development of eBPF programs and the linux kernel can be developed independently of each other, whereas a kernel module is very dependent on linux kernel development updates.


# Learning More
### eBPF
To learn about eBPF development, I would recommend starting with the BCC Python library. This is a user-space library that compiles and runs eBPF C programs. It can be installed by following [these official instructions](https://github.com/iovisor/bcc/blob/master/INSTALL.md).

To write a simple hello world program, [this article](https://rdelfin.com/blog/post_ebpf_002) should get you started. You can ignore the installation instructions as following the official BCC install instructions mentioned above will suffice. If you run into macro redefinition errors while going through this article, you can reference [this github issue](https://github.com/iovisor/bcc/issues/3366#issuecomment-1258054405).

If you want to go deeper into advanced topics, I recommend the book [Learning eBPF by Liz Rice](https://isovalent.com/learning-ebpf/)

### Linux Kernel Modules
To learn about kernel module development, [this guide](https://sysprog21.github.io/lkmpg/) is very useful. It provides a simple hello world program at the beginning and then delves into more advanced topics.


# Conclusion
Although eBPF is more rigid and provides fewer use-cases than kernel modules, it is generally easier to work with as it provides a higher-level library for interacting with kernel functionality than kernel modules would. It may be a good option to go with eBPF if the kernel you're working with supports needed features and if your use-case involves Networking, Security, or Observability. If your use-case is outside of that, you will probably have to stick with kernel modules for now. Time will tell if eBPF starts including more features to overtake use-cases that were traditionally reserved for kernel modules.
