# Bank account Kata [![Build Status](https://travis-ci.org/dambaron/bank-kata.svg?branch=master)](https://travis-ci.org/dambaron/bank-kata)

Think of your personal bank account experience When in doubt, go for the simplest solution

## Requirements
- Deposit and Withdrawal
- Account statement (date, amount, balance)
- Statement printing
 
## User Stories
##### US 1:
**In order to** save money  
**As a** bank client  
**I want to** make a deposit in my account  
 
##### US 2: 
**In order to** retrieve some or all of my savings  
**As a** bank client  
**I want to** make a withdrawal from my account  
 
##### US 3: 
**In order to** check my operations  
**As a** bank client  
**I want to** see the history (operation, date, amount, balance)  of my operations

## Installation and running
### Pre-requisites

```
$ cd /path/to/your/workspace
$ git clone https://github.com/dambaron/bank-kata.git
$ cd bank-kata
```
###Running tests
#### Unit tests

``` 
$ mvn clean install
```

#### Acceptance tests with JGiven HTML reports

``` 
$ mvn clean install
$ mvn jgiven:report
```
    
### Display HTML reports
```
$ cd /path/to/your/workspace/bank-kata/target/jgiven-reports/html  
```
Open the index.html file in your browser.