package org.mantodea.more_attributes.datas;

import java.util.HashMap;
import java.util.Map;

public class ClassData extends ConditionalContent {
    public ClassData() {
        for (var attribute : AttributeLoader.Attributes) {
            attributes.put(attribute.name, 0);
        }
    }

    public String name = "";

    public Map<String, Integer> attributes = new HashMap<>();

    public Map<String, Integer> startItems = new HashMap<>();
}
