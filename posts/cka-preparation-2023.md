---
authors:
- Iryna Chmelyk
tags:
- Kubernetes
- CKA Certification
- Cloud
- DevOps
date: 2023-12-12T11:29:50.000Z
title: "CKA certification preparation: why and how"
image: https://github.com/iryna-chmelyk/blog-usa/blob/cka-preparation/images/2023/12/cka-certificate.png
---

![Certificate](https://github.com/iryna-chmelyk/blog-usa/blob/cka-preparation/images/2023/12/cka-certificate.png)
A couple of months back I was searching for tips on how to prepare and pass the CKA (Certified Kubernetes Administrator) certification. I have looked through various materials, read quite a few discussions on this topic, and have chosen my training path. As a result, I was able to pass this exam on the first try. Now, I want to give back to the community of non-stop learners who have set their eyes on one of the "most wanted" certifications in the DevOps realm.

# Relevance


Kubernetes [(K8s)](https://kubernetes.io/) is the most widely used open source container orchestration platform on the market. Originally developed at Google, it is currently hosted by the Cloud Native Computing Foundation [(CNCF)](https://www.cncf.io/). Even though Kubernetes has been steadily gaining popularity for almost a decade, there are still not enough specialists with this skill set. More and more people are getting interested in this technology and Kubernetes-savvy engineers are in high demand. CNCF in collaboration with [The Linux Foundation](https://training.linuxfoundation.org/) has created Kubernetes Certification Programs to meet the needs of the market. What makes these exams so special besides the fact that they cover extremely sought-after knowledge areas, is that they take an entirely hands-on approach. There is no way to randomly guess your way into passing them. Not only do they certify theoretical knowledge, but they also validate practical experience working on K8s clusters.

Depending on their career path, engineers can choose between three kinds of certifications:

- [CKA](https://training.linuxfoundation.org/certification/certified-kubernetes-administrator-cka/) - Certified Kubernetes Administrator 
- [CKAD](https://training.linuxfoundation.org/certification/certified-kubernetes-application-developer-ckad/) - Certified Kubernetes Application Developer 
- [CKS](https://training.linuxfoundation.org/certification/certified-kubernetes-security-specialist/) - Certified Kubernetes Security Specialist

This post offers guidance on preparation for the CKA exam which is considered the most challenging out of three Kubernetes certifications. During this test, engineers are given access to the environment where they are faced with real-world scenarios on cluster and storage management, network and application troubleshooting, etc.


# CKA Exam Overview


Throughout the 2-hour session, exam takers need to solve 17 tasks with varying levels of difficulty. These scenarios are weighted according to their complexity (1%, 4%, 8%, etc) and a total score of at least 66% is needed to pass. The work is performed on live clusters in the terminal window and access to Kubernetes documentation is available through the whole session. It is an online, proctored exam, which means that you can take it from the comfort of your home, while a proctor will be watching you to ensure that no "extra" help is used to pass this certification. The main areas of focus in the CKA certification are:

- **Troubleshooting - 30%**
- **Cluster Architecture, Installation & Configuration - 25%**
- **Services & Networking - 20%**
- **Workloads & Scheduling - 15%**
- **Storage - 10%**

After around 6 weeks of training for 2 hours a day, I was able to pass CKA on the first attempt. The great thing about this exam is that a free retake is included in the certification package which puts the minds of those preparing for it more at ease.


# Recommended training materials


Through thorough research, I noticed that the majority of those aiming to pass CKA certification use the following training materials:

**[Highly recommended]**
- [Kubernetes documentation](https://kubernetes.io/), which is well-written and available for use during the exam. A person getting ready to tackle CKA certification should be very familiar with these pages. It is well known that time is of the essence during this test. You need to know what to do and type it fast. Reading through docs is not an option. Finding the needed snippet of code for your case is what you will use documentation for. An exam taker should be aware of what is available in the docs to be able to quickly find it.
- [Certified Kubernetes Administrator (CKA) with Practice Tests](https://www.udemy.com/course/certified-kubernetes-administrator-with-practice-tests/) (on Udemy) is great to get a grasp on theory. Also, as a part of this package, you get free access to [Udemy Labs – Certified Kubernetes Administrator with Practice Tests](https://kodekloud.com/courses/labs-certified-kubernetes-administrator-with-practice-tests/) (on KodeKloud) for hands-on labs. The way these two courses are organized is very impressive. You get theory as well as a lab environment which is extremely easy to navigate. These materials truly serve their purpose of helping learners get practical experience conveniently.
- [killer.sh CKA Simulator](https://killer.sh/), which is a well-known CKA simulator containing 25 scenarios and their solutions. It does not get more real than this. The same platform is used during the real test. As a benefit of purchasing a CKA exam, you get free access to 2 sessions in this simulator. What engineers usually do is enroll in a CKA certification program (buy the exam seat), get access to these two 36-hour sessions, and after these practice tests are used and lessons are learned, they schedule a real exam. It is recommended to do these two sessions and the exam within one week so that the context is not lost and the mind is sharp and ready. This simulator’s environment will open your eyes to the intricacies of working with the browser provided by the platform hosting the exam. For my convenience, I needed to make some tweaks in both browser and terminal settings to improve my experience and speed.
- [The Jsonpath documentation](https://kubernetes.io/docs/reference/kubectl/jsonpath/) page is worth taking a look at. There is also free training on JSON Path [here](https://kodekloud.com/courses/json-path-quiz/). This can help to save some time during an exam and it is quite useful overall while working with Kubernetes. 

**[Recommended, but optional]**
- [Kubernetes Cheatsheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/) is often mentioned as a first go-to, however, chances are that if you feel comfortable going into the exam, you already know these commands pretty well to use them without looking at this page.
- [Ultimate Certified Kubernetes Administrator (CKA) Mock Exam Series](https://kodekloud.com/courses/ultimate-certified-kubernetes-administrator-cka-mock-exam/) is a series of 10 practice tests with 20 questions each that are very useful if you want even more practice. Each session lasts for 2 hours and closely resembles real test scenarios. ‘Practice makes perfect’ as they say and in this case, practice lets you gain a fair amount of experience working on Kubernetes clusters and pass the exam with much less stress.
- [Killer Shell CKA](https://killercoda.com/killer-shell-cka) from Killercoda is an interactive platform that allows access to practice on Linux and Kubernetes-based environments from the browser. This resource is free but it also offers a plus membership for additional features. 


# Tips and Tricks


**Use imperative commands as much as possible.**
Using an imperative approach in production is definitely not a best practice. However, it will probably not be possible to pass this certification without using imperative commands for the majority of tasks because of time limitations. There are two ways to access these commands quickly. The first way is to use the cheatsheet mentioned above provided by Kubernetes documentation. The second way is to use `--help` and get examples from there. I preferred the help command as it gets tricky when you start copying and pasting from a browser into the terminal.

**Set aliases that work for you.** For me, it made sense to only use `alias k=kubectl` and `alias c=clear`. Without any sophisticated aliases like `alias kgp="kubectl get pods"` or `alias krepl="kubectl replace --force --grace-period=0"`, I was able to solve all of the tasks and had time to review my answers. I know this is quite a subjective matter, so find out which aliases work best for you, use them for practice, and remember to add them to your .bashrc file and source it.

**Bookmark the Linux Foundation [support page](https://jira.linuxfoundation.org/plugins/servlet/desk).** Just in case you need to quickly open a support request before or during the test when something seems wrong with the environment or you accidentally close an exam window (which happened to me right at the start of the test).

**Avoid getting stuck on a question.** Read through the questions thoroughly, and if you feel that you know the solution - start working on it. If you are not sure how to solve a scenario, move to the next question. The reason this is often recommended is because our brain is smart enough to work on things in the background. While moving forward with other questions, you are gaining confidence that everything is going fine, and when it is time to get back to the previously hard task, there is a chance that you have come up with a solution already.

**Be very careful about the context of your scenario.** Make a habit of switching to a new context before each task. This has been mentioned countless times all over the internet as the most common mistake.

**Get familiar with native Linux text editors.** You will need to use ***Vi, Vim, or Nano*** for editing yaml files so it is worth getting used to working effectively with one of them. Knowing a few tricks there can save you some precious time. 


# Conclusion


Interest in Kubernetes experts is steadily on the rise in today's market. Because of this, there are so many benefits to enrolling in one of the Kubernetes Certification Programs. For experienced engineers, taking these exams can be helpful to certify their knowledge and prove their professional competency. For those who have not yet had a chance to work on production clusters but are very eager to do so, choosing to get Kubernetes certified can help to gain practical experience and showcase their readiness and desire to work with this technology. Also, adding Kubernetes as a skill to your professional profile can be beneficial during a job search. <br> 
Even though this certification is not the easiest nut to crack, it is perfectly doable with a consistent approach and a fair bit of practice. The materials listed above should be sufficient to pass the exam on the first attempt. <br> 


Good luck to all soon-to-be Certified Kubernetes Administrators!

