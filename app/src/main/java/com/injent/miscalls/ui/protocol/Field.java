package com.injent.miscalls.ui.protocol;

import androidx.annotation.Nullable;

public class Field {

    public Field(String fieldName, String fieldHintText) {
        this.fieldName = fieldName;
        this.fieldHintText = fieldHintText;
    }

    private String fieldName;
    private String fieldText;
    private String fieldHintText;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldText() {
        return fieldText;
    }

    public void setFieldText(String fieldText) {
        this.fieldText = fieldText;
    }

    public String getFieldHintText() {
        return fieldHintText;
    }

    public void setFieldHintText(String fieldHintText) {
        this.fieldHintText = fieldHintText;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Field) {
            return ((Field) obj).fieldName.equals(fieldName);
        }
        return false;
    }
}
