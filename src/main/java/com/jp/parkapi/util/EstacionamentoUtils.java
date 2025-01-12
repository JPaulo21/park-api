package com.jp.parkapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstacionamentoUtils {

    private static final double PRIMEIROS_15_MINUTES = 5.00;
    private static final double PRIMEIROS_60_MINUTES = 9.25;
    private static final double ADICIONAL_15_MINUTES = 1.75;
    private static final long FIRST_HOUR = 60;
    private static final long MINUTES_COBRANCA_ADICIONAL = 15;
    private static final double DESCONTO_PERCENTUAL = 0.30;

    public static String gerarRecibo(){
        LocalDateTime date = LocalDateTime.now();
        String recibo = date.toString().substring(0, 19);
        return recibo.replace("-","")
                .replace(":", "")
                .replace("T", "-");
    }

    public static BigDecimal calcularCusto(LocalDateTime entrada, LocalDateTime saida) {
        long minutes = entrada.until(saida, ChronoUnit.MINUTES);
        double total = 0.0;

        if (minutes <= 15) {
            total = PRIMEIROS_15_MINUTES;
        } else if (minutes <= 60) {
            total = PRIMEIROS_60_MINUTES;
        } else {
            double value = (double) (minutes - FIRST_HOUR) / MINUTES_COBRANCA_ADICIONAL;
            double valorCobrancaAdicional = Math.ceil(value) * ADICIONAL_15_MINUTES;
            total = PRIMEIROS_60_MINUTES + valorCobrancaAdicional;
        }

        return new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calcularDesconto(BigDecimal custo, long numeroDeVezes) {
        System.out.println("numeroDeVezes:" + numeroDeVezes);

        BigDecimal desconto = new BigDecimal("0.00");
        if(numeroDeVezes >= 10 && numeroDeVezes % 10 == 0){
            desconto = custo.multiply(new BigDecimal(DESCONTO_PERCENTUAL));
        }


        return desconto.setScale(2, RoundingMode.HALF_EVEN);
    }
}
