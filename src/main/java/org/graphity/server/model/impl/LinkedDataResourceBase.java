/**
 *  Copyright 2012 Martynas Jusevičius <martynas@graphity.org>
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
package org.graphity.server.model.impl;

import com.hp.hpl.jena.rdf.model.*;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletConfig;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import org.graphity.server.model.LinkedDataResource;
import org.graphity.server.vocabulary.GS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class of generic read-only Linked Data resources.
 * 
 * @see <a href="http://jena.apache.org/documentation/javadoc/jena/com/hp/hpl/jena/rdf/model/Resource.html">Jena Resource</a>
 * @author Martynas Jusevičius <martynas@graphity.org>
 */
public abstract class LinkedDataResourceBase implements LinkedDataResource
{
    private static final Logger log = LoggerFactory.getLogger(LinkedDataResourceBase.class);

    private final UriInfo uriInfo;
    private final Request request;
    private final ServletConfig servletConfig;

    /** 
     * JAX-RS-compatible resource constructor with injected initialization objects.
     * The URI of the resource being created is the absolute path of the current request URI.
     * 
     * @param uriInfo URI information of the request
     * @param request current request object
     * @param servletConfig webapp context
     * @see <a href="http://docs.oracle.com/javaee/6/api/javax/ws/rs/core/UriInfo.html#getAbsolutePath()">JAX-RS UriInfo.getAbsolutePath()</a>
     */
    public LinkedDataResourceBase(@Context UriInfo uriInfo, @Context Request request, @Context ServletConfig servletConfig)
    {
	if (uriInfo == null) throw new IllegalArgumentException("UriInfo cannot be null");
	if (request == null) throw new IllegalArgumentException("Request cannot be null");
	if (servletConfig == null) throw new IllegalArgumentException("ServletConfig cannot be null");

        this.uriInfo = uriInfo;
        this.request = request;
        this.servletConfig = servletConfig;
    }
        
    /**
     * Returns response for the given RDF model.
     * 
     * @param model RDF model
     * @return response object
     */
    public Response getResponse(Model model)
    {
        return getResponseBuilder(model).build();
    }

    /**
     * Returns response builder for the given RDF model.
     * 
     * @param model RDF model
     * @return response builder
     */
    public ResponseBuilder getResponseBuilder(Model model)
    {
        return ModelResponse.fromRequest(getRequest()).
                getResponseBuilder(model, getMediaTypes(), getLanguages(), getEncodings()).
                cacheControl(getCacheControl(GS.cacheControl));
    }
    
    public MediaType[] getMediaTypes()
    {
        List<MediaType> list = org.graphity.server.MediaType.getRegisteredList();
        list.add(0, org.graphity.server.MediaType.APPLICATION_RDF_XML_TYPE); // first one becomes default
        javax.ws.rs.core.MediaType[] array = new javax.ws.rs.core.MediaType[list.size()];
        list.toArray(array);
        return array;
    }
    
    public Locale[] getLanguages()
    {
        return new Locale[]{};
    }

    public String[] getEncodings()
    {
        return new String[]{};
    }

    /**
     * Returns URI of this resource
     * 
     * @return URI of this resource
     */
    @Override
    public String getURI()
    {
	return getUriInfo().getAbsolutePath().toString();
    }

    /**
     * Returns URI information.
     * 
     * @return URI info object
     */
    public UriInfo getUriInfo()
    {
	return uriInfo;
    }

    /**
     * Returns current request.
     * 
     * @return request object
     */
    public Request getRequest()
    {
	return request;
    }

    /**
     * Returns config for this servlet (including parameters specified in web.xml).
     * 
     * @return webapp context
     */
    public ServletConfig getServletConfig()
    {
	return servletConfig;
    }

    /**
     * Returns <code>Cache-Control</code> header configuration for this resource
     * 
     * @param property cache control property
     * @return cache control of this resource
     */
    public CacheControl getCacheControl(Property property)
    {
	if (property == null) throw new IllegalArgumentException("Property cannot be null");

        if (getServletConfig().getInitParameter(property.getURI()) == null) return null;
        
        return CacheControl.valueOf(getServletConfig().getInitParameter(property.getURI()).toString());
    }
    
}