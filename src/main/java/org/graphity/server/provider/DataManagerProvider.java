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

package org.graphity.server.provider;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.util.LocationMapper;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.PerRequestTypeInjectableProvider;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import org.graphity.server.util.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martynas
 */
@Provider
public class DataManagerProvider extends PerRequestTypeInjectableProvider<Context, DataManager>
{

    private static final Logger log = LoggerFactory.getLogger(DataManagerProvider.class);

    @Context UriInfo uriInfo;
    @Context ServletContext servletContext;

    public ServletContext getServletContext()
    {
	return servletContext;
    }

    public UriInfo getUriInfo()
    {
	return uriInfo;
    }

    public DataManagerProvider()
    {
        super(DataManager.class);
    }

    @Override
    public Injectable<DataManager> getInjectable(ComponentContext cc, Context a)
    {
	return new Injectable<DataManager>()
	{
	    @Override
	    public DataManager getValue()
	    {
		return getDataManager();
	    }
	};
    }

    public DataManager getDataManager()
    {
        return getDataManager(LocationMapper.get(), ARQ.getContext(), getServletContext());
    }
    
    public DataManager getDataManager(LocationMapper mapper, com.hp.hpl.jena.sparql.util.Context context, ServletContext servletContext)
    {
        return new DataManager(mapper, context, servletContext);
    }
    
}