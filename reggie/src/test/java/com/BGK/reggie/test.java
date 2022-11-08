package com.BGK.reggie;


import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class test {
    @Test
    public void test1(){
        String s =  "(){}[]" ;

        int n = s.length();
        if (n % 2 == 1) {
            System.out.println("不匹配");
        }

        Map<Character, Character> pairs = new HashMap<Character, Character>() {{
            put(')', '(');
            put(']', '[');
            put('}', '{');
        }};
        Deque<Character> stack = new LinkedList<Character>();
        for (int i = 0; i < n; i++) {
            char ch = s.charAt(i);
            if (pairs.containsKey(ch)) {
                if (stack.isEmpty() || stack.peek() != pairs.get(ch)) {
                    System.out.println("不匹配");
                }
                stack.pop();
            } else {
                stack.push(ch);
            }
        }
        System.out.println("匹配");
    }
}
