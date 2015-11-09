/**
 *  Copyright 2014 Martynas Jusevičius <martynas@graphity.org>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.graphity.core.model.impl;

import org.graphity.core.model.Origin;

/**
 * Base class for origin implementation.
 * Origins are used to indicate remote SPARQL and Graph Store endpoints.
 * 
 * @author Martynas Jusevičius <martynas@graphity.org>
 */
public class OriginBase implements Origin
{
    
    private final String uri;
    
    /**
     * Constructs origin from URI.
     * 
     * @param uri 
     */
    public OriginBase(String uri)
    {
        this.uri = uri;
    }
    
    @Override
    public String getURI()
    {
        return uri;
    }

    @Override
    public String toString()
    {
        return uri;
    }
    
}
