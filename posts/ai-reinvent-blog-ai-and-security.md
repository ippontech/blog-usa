---
authors:
- Lucas Ward
tags:
- AWS
- Security
- AI
date: 2023-11-07T17:54:01.000Z
title: "Elevate Your Security Game with Amazon Security Lake and Amazon Kendra"
image: 
---

For most organizations, cybersecurity has always been an after thought. It typically takes a cataclysmic event happening to get companies to start taking security seriously, such as a ransomware attack. As we all know though, no matter how good your cybersecurity posture is, the human element has been and always will be the Achilles' heel. In today's fast-paced digital world, getting a leg up on cybersecurity entails meeting the users half way between policy and best practices. Today I want to tell you about two tools that can seriously change the cyber security game and give you a leg up on those threat actors: Amazon Security Lake and Amazon Kendra.

# The Human Factor: Security's Constant Nemesis

We must acknowledge that the biggest vulnerability to security remains to be the human element. The level of sophistication being employed by threat actors to exploit human weakness is astonishing. Threat actors use AI every day to generate scam emails, fake websites, and even convincing voice agents for phone scams. I think it's time we evened the playing field a bit and start leveraging AI to help us humans with our cybersecurity woes. It is now, more than ever, essential for cybersecurity teams to have the right tools at their disposal to counteract threat actors effectively.

# Open Cybersecurity Schema Framework (OCSF): Bridging the Gap

Before we can put AI to work solving our security struggles, we must first adopt a common language for our security data. Enter the Open Cybersecurity Schema Framework (OSCF). OSCF seeks to normalize and combine multiple disparate sources of security data. This can include raw data from security tools, data from AWS Services, or data from insight engines like AWS Security Hub. We a common language put in place, we are now positioned to leverage the advancements in generative AI to provide contextual help surrounding security anomalies.

# Introducing AWS Security Lake

So you have loads of different security sources across regions and accounts in AWS. You also have a common language to store this data. But where do we put it? This is where AWS Security Lake comes into play. It collects findings from various AWS services like Amazon Guard Duty, Amazon inspector, IAM Access analyzer, and loads of partners solutions. It's like a regular data lake, but specifically for security data.

With this data all in one place, the ease of analysis is greatly increased. Using powerful tools in AWS like Athena, Opensearch, and SageMaker, you can gain actionable insights, draw correlations, and detect anomalies. This all happens without having to sift through logs and hunt for the one liners. But how does this play into the human element? This is still all just one system analyzing the output of another system... What we really need is the insights combined with contextual guidance.

# Contextual Guidance: A Must-Have

As I am sure you are well aware, it's not just about the data and the insights gained there within. It is also about taking those security insights, providing context, and giving them to the builders in your organization. And now, with Amazon Kendra, this process can be mostly automated and improved upon. 

Using powerful AI-driven search and Retrieval Augmented Generation, Amazon kendra can combine forces with AWS Security Lake and Amazon Bedrock to offer contextual guidance like never before. In other words, that "security issue" that was detected, means a lot more when elevated with the context of *which company policy* it violated, or *which publicly available mitigation technique* should be leveraged to resolve the issue. The best part about all of this is by far the reduced cognitive load placed on security engineers, freeing them up to focus on high-value high-impact opportunities. 

# Getting Started

So, how can your security team begin to safely use generative AI to enhance their capabilities? First and foremost, embrace the power of Amazon Security Lake to centralize your security data. With this consolidated data at your fingertips, leverage the capabilities of Amazon Kendra to provide contextual guidance to your security team and builders.

By doing so, you'll be on the path to reducing human error and increasing your ability to tackle novel, high-value opportunities. It's time to take your security game to the next level and stay one step ahead of those ever-evolving threats! If the bad guys can use AI to enhance their threats, than we give our selves permission to use it to stop them as well!

# Conclusion

Amazon Security Lake and Amazon Kendra are dynamic duo that can significantly elevate your organization's security posture. The Open Cybersecurity Schema Framework, AWS Security Lake, and Amazon Kendra work together to streamline data collection, analysis, and contextual guidance, making your security team more effective and agile. Say goodbye to outdated security practices and embrace the future of cybersecurity with Amazon Web Services. If you work for an organization with big generative AI ambitions and want to leverage the expertise of Ippon Technologies Consultants, then drop us a line at sales@ipponusa.com.