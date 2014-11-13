package com.mlucky.coin.app.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by m.iakymchuk on 05.11.2014.
 */
public class TestApplication {
    public static final String titleSalary = "Salary";
    public static final String titleTaxi = "Taxi";
    public static final String titleAgricole = "Agricole";
    public static final String titleCash = "Cash";

    public static final String titleFood = "Food";
    public static final String titleClose = "Close";
    public static final String titleCar = "Car";

    @Test
    public void testApplicationAdding() {
        CoinApplication coinApplication = CoinApplication.getCoinApplication();

        InCome salary = coinApplication.addIncome(titleSalary);
        coinApplication.addIncome(titleTaxi);

        Account agricole = coinApplication.addAccount(titleAgricole);
        coinApplication.addAccount(titleCash);

        coinApplication.addSpend(titleFood);
        coinApplication.addSpend(titleClose);

        coinApplication.addGoal(titleCar);


        coinApplication.addInComeAccountTransaction(salary, agricole, "840");
        System.out.println(salary.getTotal().toString());
        System.out.println(agricole.getTotal().toString());
        Assert.assertEquals(salary.getTotal().toString(),"UAH 840.00");
        Assert.assertEquals(agricole.getTotal().toString(),"UAH 840.00");
    }
}
