package unicaferater.domain.common;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by jani on 14.12.2014.
 */

@Entity
public class Price extends AbstractPersistable<Long> {

    @Column(unique = true)
    private String name;

    @OneToOne
    private PriceValue value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PriceValue getValue() {
        return value;
    }

    public void setValue(PriceValue value) {
        this.value = value;
    }

    @JsonSetter("value")
    public void setValueFromJson(JsonNode valueNode) {
        if (valueNode == null || valueNode.isNull() || valueNode.isArray()) {
            this.value = null;
            return;
        }

        if (!valueNode.isObject()) {
            this.value = null;
            return;
        }

        PriceValue parsedValue = new PriceValue();
        parsedValue.setContract(readText(valueNode, "contract"));
        parsedValue.setGraduate(readText(valueNode, "graduate"));
        parsedValue.setGraduate_hyy(readText(valueNode, "graduate_hyy"));
        parsedValue.setNormal(readText(valueNode, "normal"));
        parsedValue.setStudent(readText(valueNode, "student"));
        parsedValue.setStudent_hyy(readText(valueNode, "student_hyy"));
        this.value = parsedValue;
    }

    private String readText(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }

        return fieldNode.asText();
    }
}
