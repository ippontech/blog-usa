---
authors:
- Dennis Sharpe
  tags:
- FedNow
- Payments
- ISO20022
- FederalReserveBank
  date: 2022-05-27T12:21:50.000Z
  title: "Instant Payments with FedNow"
  image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/05/instant-payments-with-fednow.png
---
Trying to map the path of technological advancement can be a daunting task. Why did this advancement come before this *other* advancement? Sometimes many levels of depth are explored in one subject area while other areas are left woefully behind. This is true for one of the backbones of currency: payments.

Why, in this modern age, does it still take several days for a payment to clear? Why can’t I pay my electric bill on the day it is due? Payments have been devoid of innovation for decades. The last major innovation was the near-elimination of paper checks. Now is the time for instant payments.

# Introducing FedNow
The Federal Reserve Banks are introducing a new instant payment service called [FedNow](https://explore.fednow.org/) that will enable financial institutions of every size, and in every community across the U.S., to provide safe and efficient instant payments in real-time, around the clock, every day of the year.

![About FedNow Payment Flow](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/05/about-fednow-payments-flow.jpeg)

Bank customers win with the following benefits:
- Instantly paying a bill
- Reducing payment errors
- Eliminating bounced checks

Banks also win by:
- Taking back business from Zelle and Venmo
- Delighting their customers
- Saving on insufficient fund processing costs

A few of you might be asking, “But what about Venmo and the RTP Network?” Venmo is a closed network. To actually pull money out of Venmo so that you can use it takes the same time to clear as a standard payment. Venmo, the company, completely controls that system. Meanwhile, the RTP Network and Zelle are controlled by a select group of banks. The RTP Network also does not support “instant” payments, but rather just “faster” payments.

# Request for Payment
FedNow also introduces the Request for Payment (RFP). The RFP takes the place of a traditional invoice. An electronic RFP is sent via the FedNow system and it contains all the details regarding the payment amount, destination, etc. The idea is that all the details are handled with software so there is much less room for error when submitting a payment. This should eliminate any sort of manual entry.

# Cost
- A $25 monthly FedNow Service participation fee for each routing transit number (RTN) that enrolls to receive credit transfers in the service
- A fee of $0.045 per transaction to be paid by its sender, including returns
- A fee of $0.01 for an RFP message to be paid by the requestor, including both requests for a new payment or funds to be returned. A business can send an RFP through the FedNow Service to a participating financial institution to request payment of a bill, invoice, or other amount owed by the receiving financial institution’s customer.

By comparison, Zelle charges between $0.50 and $0.75 per transaction between banks. For Venmo, there is a 1.75% (minimum $0.25, maximum $25) fee for electronic withdrawal (instant transfers).

# Transaction Limits
There is a default limit of $100,000 per transaction, but that can be raised (via request) to $500,000 if necessary.

# Fraud
Enabling instant payments does introduce a new challenge with regard to fraud detection. Previously, long batch processes could run to attempt to detect fraud. Now fraud detection must be completed in seconds. Those in the credit card business have a leg up because credit card authorizations must already be analyzed for fraud in seconds. The Federal Reserve is providing tools like the [FraudClassifier Model](https://fedpaymentsimprovement.org/strategic-initiatives/payments-security/fraudclassifier-model/) to help detect fraud.

# Let's Get Technical
Do you remember when XML was the latest craze and SOAP web services sprang up everywhere? Well, FedNow messages are a throwback to that craze and are all in the XML format. It just so happens that for the use cases required for instant payments, XML makes a lot of sense. We get a fully detailed schema that messages can be validated against. We can also use an international standard called [ISO 20022](https://www.iso20022.org/). This standard is currently used or planned for use by multiple payment systems around the world.

# Conclusion
FedNow is set to go live in 2023, so now is the time to prepare and not be left behind.

There are several benefits with FedNow that truly are game changers for payments. The first is the ability for every financial institution, large or small, to participate. The second relates to the RFP. This will have a huge impact on Accounts Receivables by drastically reducing the error rate for incoming payments.

Payments technology will finally be keeping up with all the other technological advances in finance.

If you want to learn more about FedNow or want help getting started, please let me know!
