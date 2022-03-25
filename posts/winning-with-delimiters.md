---
authors:
- Greg Kontos
tags:
- csv
- tsv
- File Delimiters
- SnapLogic
- Unicode
- AWS Glue
- Python
title: "Winning Migrations with Good Delimiters"
date: 2022-03-04T13:59:40-04:00
---
# Winning Migrations with Epic Delimiters

On a recent project, we have been helping a customer develop an approach to migrate ETL pipelines from SnapLogic to AWS Glue.  Among all of the refactoring and replatforming decisions, we noticed that some SnapLogic pipelines use the .tsv file delimiter of `\u001`.  In SnapLogic, this delimiter outputs a "start of heading" or "SOH" character.  However, `\u001` is [not a valid character](https://www.charset.org/utf-8) and will not parse in Python.  This kicked off some discussion around delimiter best practices and Unicode characters.  

In Python you must use either `\u0001` (unicode ) or `\001` (octal) to get the same SOH character used by SnapLogic, `\u001`.  This is good news for the migration since the output format of the files can stay the same.  While this a functional solution, it is not a satisfying solution.  The `\u001` character is [not a character](https://donsnotes.com/tech/charsets/ascii.html) in [unicode](https://en.wikipedia.org/wiki/List_of_Unicode_characters) ( `\u0001` or `U+0001`), UTF-8 (01), octal (`\001`), or hexidecimal (`\x01`).  This non-character caused unexpected time debugging unicode in unit tests, and eventually some deep reflection.  That reflection uncovered a key principle: A non-standard, let alone invalid, approach should have clear benefits to justify issues with integrations or migrations.  The reflection also revealed that we rarely think about file delimiters until it's too late.  

Is `\u001`, or another non-standard character, a better field delimiter than the humble comma or tab?  Why would `\u001` be used as a delimiter? The simplest answer as to why it was used is that the `\u001` character is referenced in the SnapLogic documentation.  From the docs:
```
The Delimiter property specifies the character to be used as a delimiter in parsing the CSV data. In case that tab is used as a delimiter, enter "\t" instead of pressing the Tab key. Any Unicode character is also supported.  As of 4.3.2, this property can be an expression, which is evaluated with the values from the pipeline parameters.
Example: \t, \u001
Default value: ,
Format: Character or Unicode
```

Perhaps it was chosen to save space? All UTF-8 characters will occupy between 1 and 4 bytes on disc. In the case of the SOH character, this is a single byte.  [Comma and Tab are also single byte characters in UTF-8](https://naveenr.net/unicode-character-set-and-utf-8-utf-16-utf-32-encoding/#:~:text=The%20binary%20value%20of%20the,representation%20of%20%C3%B1%20shown%20below.). At first glance, there is no space benefit to using SOH a delimiter over a comma or tab.  However, if `\u0001` is used as a the separator and it is not enclosed in quotes, the file will be smaller by the number of quote and escape characters needed.  If disc space can be saved by avoiding quotes, then SOH is a clear winner.  

Which leads us to the most unlikely of google searches 'best practices for quoting and escaping'. According to [documentation from Snowflake](https://docs.snowflake.com/en/user-guide/data-load-considerations-prepare.html) and a [very good, concise post on the subject](https://old.dataone.org/best-practices/use-appropriate-field-delimiters) best practice is for all fields to be enclosed in quotes if they could contain the delimiter, no matter which delimiter is used.  And while unusual, the SOH ( `\u0001` ) character could appear in a string field within the file.  Therefore those fields should be enclosed by quotes and the quotes should be escaped.  The cost of any disc space saved by not enclosing strings is probably offset by any support issues arising from stray characters in the file.  

Since all fields should be quoted and escaped, the winning move is to use the humble comma or tab as delimiters.  These choices also have the benefit of being easier to work with as standard delimiters are expected by more systems and software.  While the standard delimiters should be the go to for any organization a key take away from this research, however, is that there are always exceptions.  If you are in a crunch for disc space or network bandwidth using `\u0001` is a solid trick you can use to trim bytes.  

A key aspect to software engineering is weighing the costs and benefits of various decisions.  In this case we need to decide if costs should be optimized by avoiding errors or by reducing disc consumption.  Under ideal circumstances these decision points can be identified ahead of time and we make proactive plans.  Sometimes we don't realize until after the fact that a decision was made.  A great software engineering partner can help bring value based decisioning into the software development process by identifying trade offs rather than blindly following established patterns.   