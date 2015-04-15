/*
 * Copyright 2014, Stratio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.cassandra.index.schema.mapping;

import com.google.common.base.Objects;
import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.db.marshal.AsciiType;
import org.apache.cassandra.db.marshal.DecimalType;
import org.apache.cassandra.db.marshal.DoubleType;
import org.apache.cassandra.db.marshal.FloatType;
import org.apache.cassandra.db.marshal.Int32Type;
import org.apache.cassandra.db.marshal.IntegerType;
import org.apache.cassandra.db.marshal.LongType;
import org.apache.cassandra.db.marshal.UTF8Type;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ColumnMapper} to map a float field.
 *
 * @author Andres de la Pena <adelapena@stratio.com>
 */
public class ColumnMapperFloat extends ColumnMapperSingle<Float> {

    /** The default boost. */
    public static final Float DEFAULT_BOOST = 1.0f;

    /** The boost. */
    private final Float boost;

    /**
     * Builds a new {@link ColumnMapperFloat} using the specified boost.
     *
     * @param boost The boost to be used.
     */
    @JsonCreator
    public ColumnMapperFloat(@JsonProperty("boost") Float boost) {
        super(new AbstractType<?>[]{AsciiType.instance,
                                    UTF8Type.instance,
                                    Int32Type.instance,
                                    LongType.instance,
                                    IntegerType.instance,
                                    FloatType.instance,
                                    DoubleType.instance,
                                    DecimalType.instance}, new AbstractType[]{FloatType.instance});
        this.boost = boost == null ? DEFAULT_BOOST : boost;
    }

    /** {@inheritDoc} */
    @Override
    public Float toLucene(String name, Object value, boolean checkValidity) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof String) {
            try {
                return Double.valueOf((String) value).floatValue();
            } catch (NumberFormatException e) {
                // Ignore to fail below
            }
        }
        String message = String.format("Field \"%s\" requires a float, but found \"%s\"", name, value);
        throw new IllegalArgumentException(message);
    }

    /** {@inheritDoc} */
    @Override
    public List<Field> fieldsFromBase(String name, Float value) {
        List<Field> fields = new ArrayList<>();
        fields.add(new FloatField(name, value, STORE));
        fields.add(new NumericDocValuesField(name, Float.floatToIntBits(value)));
        return fields;
    }

    /** {@inheritDoc} */
    @Override
    public SortField sortField(String field, boolean reverse) {
        return new SortField(field, Type.FLOAT, reverse);
    }

    /** {@inheritDoc} */
    @Override
    public Class<Float> baseClass() {
        return Float.class;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("boost", boost).toString();
    }

}
