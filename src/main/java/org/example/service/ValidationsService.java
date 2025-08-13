package org.example.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ValidationsService {

    Pattern mailPattern = Pattern.compile("([a-zA-Z0-9._%-]+)@([a-zA-Z0-9.-]+).([a-zA-Z]{2,6})");
    Pattern pssPattern = Pattern.compile("^(?=(.*[a-z]){5,10})(?=(?:[^A-Z]*[A-Z]){1}[^A-Z]*$)" +
            "(?=(?:[^0-9]*[0-9]){2}[^0-9]*$)[a-zA-Z0-9]{8,12}$");

    public String validationParams(String validEmail, String validPss){
        boolean casesResult = mailPattern.matcher(validEmail).matches() && pssPattern.matcher(validPss).matches();
        String mail =  mailPattern.matcher(validEmail).matches()? " VALID EMAIL *** " : " INVALID EMAIL *** ";
        String pass =  pssPattern.matcher(validPss).matches()? " VALID PASSWORD *** " : " INVALID PASSWORD *** " ;
        System.out.println(mail);
        System.out.println(pass);
        StringBuilder sbuilder = new StringBuilder().append(casesResult).append(mail).append(pass);
        return sbuilder.toString();
    }
}