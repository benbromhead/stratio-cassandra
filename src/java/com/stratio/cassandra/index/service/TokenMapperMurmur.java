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
package com.stratio.cassandra.index.service;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.dht.Murmur3Partitioner;
import org.apache.cassandra.dht.Token;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

/**
 * {@link PartitionKeyMapper} to be used when {@link Murmur3Partitioner} is used. It indexes the token long value as a
 * Lucene long field.
 *
 * @author Andres de la Pena <adelapena@stratio.com>
 */
public class TokenMapperMurmur extends TokenMapper {

    private static final String FIELD_NAME = "_token_murmur"; // The Lucene field name

    /**
     * Builds a new {@link TokenMapperMurmur} using the specified {@link CFMetaData}.
     *
     * @param metadata A column family metadata.
     */
    public TokenMapperMurmur(CFMetaData metadata) {
        super(metadata);
    }

    /** {@inheritDoc} */
    @Override
    public void addFields(Document document, DecoratedKey partitionKey) {
        Long value = (Long) partitionKey.getToken().getTokenValue();
        Field tokenField = new LongField(FIELD_NAME, value, Store.NO);
        document.add(tokenField);
    }

    /** {@inheritDoc} */
    @Override
    public Query query(Token token) {
        Long value = (Long) token.getTokenValue();
        return NumericRangeQuery.newLongRange(FIELD_NAME, value, value, true, true);
    }

    /** {@inheritDoc} */
    @Override
    protected Query makeQuery(Token lower, Token upper, boolean includeLower, boolean includeUpper) {
        Long start = lower == null ? null : (Long) lower.getTokenValue();
        Long stop = upper == null ? null : (Long) upper.getTokenValue();
        if (lower != null && lower.isMinimum()) {
            start = null;
        }
        if (upper != null && upper.isMinimum()) {
            stop = null;
        }
        if (start == null && stop == null) {
            return null;
        }
        return NumericRangeQuery.newLongRange(FIELD_NAME, start, stop, includeLower, includeUpper);
    }

    /** {@inheritDoc} */
    @Override
    public SortField[] sortFields() {
        return new SortField[]{new SortField(FIELD_NAME, SortField.Type.LONG)};
    }

}
