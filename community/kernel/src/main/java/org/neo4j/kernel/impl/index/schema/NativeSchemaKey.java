/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.index.schema;

import org.neo4j.index.internal.gbptree.GBPTree;
import org.neo4j.values.storable.Value;
import org.neo4j.values.storable.ValueWriter;

/**
 * Includes value and entity id (to be able to handle non-unique values).
 * This is the abstraction of what NativeSchemaIndex with friends need from a schema key.
 * Note that it says nothing about how keys are compared, serialized, read, written, etc. That is the job of Layout.
 */
abstract class NativeSchemaKey extends ValueWriter.Adapter<RuntimeException>
{
    static final boolean DEFAULT_COMPARE_ID = true;

    private long entityId;
    private boolean compareId = DEFAULT_COMPARE_ID;

    /**
     * Marks that comparisons with this key requires also comparing entityId, this allows functionality
     * of inclusive/exclusive bounds of range queries.
     * This is because {@link GBPTree} only support from inclusive and to exclusive.
     * <p>
     * Note that {@code compareId} is only an in memory state.
     */
    void setCompareId( boolean compareId )
    {
        this.compareId = compareId;
    }

    boolean getCompareId()
    {
        return compareId;
    }

    long getEntityId()
    {
        return entityId;
    }

    void setEntityId( long entityId )
    {
        this.entityId = entityId;
    }

    final void from( long entityId, Value... values )
    {
        compareId = DEFAULT_COMPARE_ID;
        this.entityId = entityId;
        from( values );
    }

    abstract void from( Value[] values );

    String propertiesAsString()
    {
        return asValue().toString();
    }

    abstract Value asValue();

    final void initAsLowest()
    {
        compareId = DEFAULT_COMPARE_ID;
        entityId = Long.MIN_VALUE;
        initValueAsLowest();
    }

    abstract void initValueAsLowest();

    final void initAsHighest()
    {
        compareId = DEFAULT_COMPARE_ID;
        entityId = Long.MAX_VALUE;
        initValueAsHighest();
    }

    abstract void initValueAsHighest();
}
