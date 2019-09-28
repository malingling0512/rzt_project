package com.mll.mll_project.face;

import java.util.Map;

public interface Action {
    String doPost(String body);

    String doGet(Map<String, String> parms);
}
