---
authors:
- Greg Kontos
tags:
- csv
- tsv
- File Delimiters
- SnapLogic
- Unicode
- aws glue
- python
title: "Winning Migrations with Good Delimiters"
date: 2022-03-04T13:59:40-04:00
---
# Winning Migrations with Epic Delimiters

On a recent project we have been helping a customer develop an approach to migrate ETL pipelines from Snaplogic to AWS Glue.  Among all of the refactoring and replatforming decisions, we noticed that some Snaplogic pipelines use the tsv file delimiter of `\u001`.  In Snaplogic this delimiter outputs a "start of heading" or "SOH" character.  However, `\u001` is [not a valid character](https://www.charset.org/utf-8) and will not parse in python.  This kicked off some discussion around delimiter best practices and unicode characters.  

In python you must use either `\u0001` (unicode ) or `\001` (octal) to get the same SOH character.  The good news is that the Snaplogic `\u001` and the python characters `\u0001` or `\001` will all output the same character, so there is a good 1 to 1 path for mirroring the existing delimiters.  But the `\u001` character is [not a character](https://donsnotes.com/tech/charsets/ascii.html) in [unicode](https://en.wikipedia.org/wiki/List_of_Unicode_characters) ( `\u0001` or `U+0001`), UTF-8 (01), octal (`\001`), or hexidecimal (`\x01`).  This started a question of why was a the non-character `\u001` used as a field delimiter and should it be used?

The simplest answer as to why it was used is that the `\u001` character is referenced in the Snaplogic documentation.  From the docs:
```
The Delimiter property specifies the character to be used as a delimiter in parsing the CSV data. In case that tab is used as a delimiter, enter "\t" instead of pressing the Tab key. Any Unicode character is also supported.  As of 4.3.2, this property can be an expression, which is evaluated with the values from the pipeline parameters.
Example: \t, \u001
Default value: ,
Format: Character or Unicode
```

Perhaps it was chosen to save space? All UTF-8 characters will occupy between 1 and 4 bytes on disc. In the case of the SOH character, this is a single byte.  [Comma and Tab are also single byte characters in UTF-8](https://naveenr.net/unicode-character-set-and-utf-8-utf-16-utf-32-encoding/#:~:text=The%20binary%20value%20of%20the,representation%20of%20%C3%B1%20shown%20below.) . So there is no space benefit to using SOH a delimiter over a comma or tab.  However, if `\u0001` is used as a the separator and it is not enclosed in quotes, the file will be smaller by the number of quote and escape characters needed.  So there may be some logic to saving disc space with an uncommon field delimiter.

But, what are the best practices for delimiters? Looking through some [documentation from Snowflake](https://docs.snowflake.com/en/user-guide/data-load-considerations-prepare.html) and a [very good, concise post on the subject](https://old.dataone.org/best-practices/use-appropriate-field-delimiters) it appears that best practice is for all fields to be enclosed in quotes if they could contain the delimiter, no matter which delimiter is used.  And while very unlikely, the SOH ( `\u0001` ) character could appear in a string field within the file.  So those fields should be enclosed by quotes and the quotes should be escaped.  The cost of any disc space saved by not enclosing strings is probably offset by any support issues arising from an errant character in the file.  Since all fields should be quoted and escaped, the winning ETL move is to use comma and tab as delimiters because they are more standard and are expected by more systems and software.  