package br.edu.ifpb.pweb2.bloomfinance.config;


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToBigDecimalConverter implements Converter<String, BigDecimal> {

    @Override
    public BigDecimal convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        try {
            NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
            Number number = format.parse(source.trim());
            return BigDecimal.valueOf(number.doubleValue());
        } catch (ParseException e) {
            throw new RuntimeException("Erro ao converter valor monetário: " + source, e);
        }
    }
}
