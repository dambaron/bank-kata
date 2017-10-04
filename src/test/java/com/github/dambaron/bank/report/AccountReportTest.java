package com.github.dambaron.bank.report;

import com.github.dambaron.bank.model.Account;
import com.github.dambaron.bank.model.Statement;
import com.github.dambaron.bank.model.operation.DepositOperation;
import com.github.dambaron.bank.model.operation.WithdrawalOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountReportTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountReportTest.class);

    private static final Currency EURO_CURRENCY = Currency.getInstance("EUR");

    private static final String EXPECTED_REPORT_FRANCE = "" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|Account: AAAAAAAA-AAAA-AAAA-AAAA-AAAAAAAAAAAA                                                                         |\n" +
            "|Owner: BBBBBBBB-BBBB-BBBB-BBBB-BBBBBBBBBBBB                                                                           |\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|Date              ||Operation                             ||                     Deposit||                  Withdrawal|\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|1970-01-01        ||CCCCCCCC-CCCC-CCCC-CCCC-CCCCCCCCCCCC  ||                    123,00 €||                            |\n" +
            "|1970-01-02        ||DDDDDDDD-DDDD-DDDD-DDDD-DDDDDDDDDDDD  ||                            ||                    987,00 €|\n" +
            "|1970-01-03        ||EEEEEEEE-EEEE-EEEE-EEEE-EEEEEEEEEEEE  ||                    456,00 €||                            |\n" +
            "|1970-01-04        ||FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF  ||                            ||                    654,00 €|\n" +
            "|1970-01-05        ||GGGGGGGG-GGGG-GGGG-GGGG-GGGGGGGGGGGG  ||                    789,00 €||                            |\n" +
            "|1970-01-06        ||HHHHHHHH-HHHH-HHHH-HHHH-HHHHHHHHHHHH  ||                            ||                    321,00 €|\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|Balance                                                                                 ||            123 456 789,00 €|\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n";

    private static final String EXPECTED_REPORT_US = "" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|Account: AAAAAAAA-AAAA-AAAA-AAAA-AAAAAAAAAAAA                                                                         |\n" +
            "|Owner: BBBBBBBB-BBBB-BBBB-BBBB-BBBBBBBBBBBB                                                                           |\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|Date              ||Operation                             ||                     Deposit||                  Withdrawal|\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|1970-01-01        ||CCCCCCCC-CCCC-CCCC-CCCC-CCCCCCCCCCCC  ||                     $123.00||                            |\n" +
            "|1970-01-02        ||DDDDDDDD-DDDD-DDDD-DDDD-DDDDDDDDDDDD  ||                            ||                     $987.00|\n" +
            "|1970-01-03        ||EEEEEEEE-EEEE-EEEE-EEEE-EEEEEEEEEEEE  ||                     $456.00||                            |\n" +
            "|1970-01-04        ||FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF  ||                            ||                     $654.00|\n" +
            "|1970-01-05        ||GGGGGGGG-GGGG-GGGG-GGGG-GGGGGGGGGGGG  ||                     $789.00||                            |\n" +
            "|1970-01-06        ||HHHHHHHH-HHHH-HHHH-HHHH-HHHHHHHHHHHH  ||                            ||                     $321.00|\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|Balance                                                                                 ||             $123,456,789.00|\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n";

    private static final String EXPECTED_REPORT_UK = "" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|Account: AAAAAAAA-AAAA-AAAA-AAAA-AAAAAAAAAAAA                                                                         |\n" +
            "|Owner: BBBBBBBB-BBBB-BBBB-BBBB-BBBBBBBBBBBB                                                                           |\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|Date              ||Operation                             ||                     Deposit||                  Withdrawal|\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|1970-01-01        ||CCCCCCCC-CCCC-CCCC-CCCC-CCCCCCCCCCCC  ||                     £123.00||                            |\n" +
            "|1970-01-02        ||DDDDDDDD-DDDD-DDDD-DDDD-DDDDDDDDDDDD  ||                            ||                     £987.00|\n" +
            "|1970-01-03        ||EEEEEEEE-EEEE-EEEE-EEEE-EEEEEEEEEEEE  ||                     £456.00||                            |\n" +
            "|1970-01-04        ||FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF  ||                            ||                     £654.00|\n" +
            "|1970-01-05        ||GGGGGGGG-GGGG-GGGG-GGGG-GGGGGGGGGGGG  ||                     £789.00||                            |\n" +
            "|1970-01-06        ||HHHHHHHH-HHHH-HHHH-HHHH-HHHHHHHHHHHH  ||                            ||                     £321.00|\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n" +
            "|Balance                                                                                 ||             £123,456,789.00|\n" +
            "|----------------------------------------------------------------------------------------------------------------------|\n";

    @Test
    public void test_toStringReport() {
        //given
        String accountId = "AAAAAAAA-AAAA-AAAA-AAAA-AAAAAAAAAAAA";
        String ownerId = "BBBBBBBB-BBBB-BBBB-BBBB-BBBBBBBBBBBB";

        Account account = Mockito.spy(new Account.Builder(ownerId, EURO_CURRENCY)
                .overdraft(BigDecimal.ZERO)
                .balance(new BigDecimal(123456789))
                .build());

        when(account.getId()).thenReturn(accountId);
        when(account.getOwnerId()).thenReturn(ownerId);

        //Spying operations to ensure constant operation id
        DepositOperation d1 = Mockito.spy(new DepositOperation(accountId, EURO_CURRENCY, new BigDecimal(123)));
        DepositOperation d2 = Mockito.spy(new DepositOperation(accountId, EURO_CURRENCY, new BigDecimal(456)));
        DepositOperation d3 = Mockito.spy(new DepositOperation(accountId, EURO_CURRENCY, new BigDecimal(789)));

        when(d1.getId()).thenReturn("CCCCCCCC-CCCC-CCCC-CCCC-CCCCCCCCCCCC");
        when(d2.getId()).thenReturn("EEEEEEEE-EEEE-EEEE-EEEE-EEEEEEEEEEEE");
        when(d3.getId()).thenReturn("GGGGGGGG-GGGG-GGGG-GGGG-GGGGGGGGGGGG");

        List<DepositOperation> deposits = Arrays.asList(d1, d2, d3);

        WithdrawalOperation w1 = Mockito.spy(new WithdrawalOperation(accountId, EURO_CURRENCY, new BigDecimal(987)));
        WithdrawalOperation w2 = Mockito.spy(new WithdrawalOperation(accountId, EURO_CURRENCY, new BigDecimal(654)));
        WithdrawalOperation w3 = Mockito.spy(new WithdrawalOperation(accountId, EURO_CURRENCY, new BigDecimal(321)));

        when(w1.getId()).thenReturn("DDDDDDDD-DDDD-DDDD-DDDD-DDDDDDDDDDDD");
        when(w2.getId()).thenReturn("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
        when(w3.getId()).thenReturn("HHHHHHHH-HHHH-HHHH-HHHH-HHHHHHHHHHHH");

        List<WithdrawalOperation> withdrawals = Arrays.asList(w1, w2, w3);

        BigDecimal startBalance = account.getBalance();
        BigDecimal endBalance;

        List<Statement> statements = new ArrayList<>();

        Instant statementInstant = Instant.EPOCH;
        for (int i = 0; i < 3; i++) {

            DepositOperation d = deposits.get(i);
            WithdrawalOperation w = withdrawals.get(i);

            endBalance = startBalance.add(d.getAmount());

            Statement depositStatement = new Statement.Builder(accountId, startBalance, d)
                    .instant(statementInstant)
                    .endBalance(endBalance)
                    .build();

            startBalance = endBalance;
            endBalance = startBalance.subtract(w.getAmount());

            statementInstant = statementInstant.plus(1, ChronoUnit.DAYS);

            Statement withdrawalStatement = new Statement.Builder(accountId, startBalance, w)
                    .instant(statementInstant)
                    .endBalance(endBalance)
                    .build();

            statements.add(depositStatement);
            statements.add(withdrawalStatement);

            statementInstant = statementInstant.plus(1, ChronoUnit.DAYS);
        }

        account.setStatements(statements);

        AccountReport accountReportFrance = new AccountReport(account, Locale.FRANCE);
        AccountReport accountReportUS = new AccountReport(account, Locale.US);
        AccountReport accountReportUK = new AccountReport(account, Locale.UK);

        //when
        String actualReportFrance = accountReportFrance.toStringReport();
        String actualReportUS = accountReportUS.toStringReport();
        String actualReportUK = accountReportUK.toStringReport();

        //then
        assertThat(actualReportFrance).isEqualTo(EXPECTED_REPORT_FRANCE);
        assertThat(actualReportUS).isEqualTo(EXPECTED_REPORT_US);
        assertThat(actualReportUK).isEqualTo(EXPECTED_REPORT_UK);


        System.out.println();
    }
}