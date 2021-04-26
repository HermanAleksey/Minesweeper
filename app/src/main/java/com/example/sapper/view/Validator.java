package com.example.sapper.view;

public class Validator {

    // -----------RegExp for email ------------
    /*
    Содержит символ "@"
    содержит точку после него
     */
    public boolean matchEmail(String str) {
        String regexpEmail = ".+@.+\\..+";
        return str.matches(regexpEmail);
    }

    // -----------RegExp for password ------------
    /*
    Содержит только латинские буквы и цифры
    Должен начинаться с буквы
    Минимум 8 символов
     */
    public boolean matchPassword(String str){
//        String regexpPassword = "([a-z]+\\w{7,})|([A-Z]+\\w{7,})";
        String regexpPassword = "\\w{4,}";
        return str.matches(regexpPassword);
    }

    // -----------RegExp for nickname ------------
    /*
    Содержит только латинские буквы и цифры
    Минимум 4 символа
     */
    public boolean matchNickname(String str){
        String regexpNick = "\\w{4,}";
        return str.matches(regexpNick);
    }

}
