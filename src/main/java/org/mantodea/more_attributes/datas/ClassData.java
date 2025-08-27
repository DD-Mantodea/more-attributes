package org.mantodea.more_attributes.datas;

import java.util.HashMap;
import java.util.Map;

public class ClassData extends ConditionalContent {
    public ClassData() {
        attributes.put("strength", 0);
        attributes.put("health", 0);
        attributes.put("focus", 0);
        attributes.put("stamina", 0);
        attributes.put("endurance", 0);
        attributes.put("intelligence", 0);
        attributes.put("agility", 0);
        attributes.put("skill", 0);
        attributes.put("luck", 0);
    }

    public String name = "";

    public Map<String, Integer> attributes = new HashMap<>();

    public Map<String, Integer> startItems = new HashMap<>();
}
