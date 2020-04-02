/*
 * Copyright 2019 Martynas Jusevičius <martynas@atomgraph.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atomgraph.core.client;

import com.atomgraph.core.MediaTypes;
import com.atomgraph.core.exception.ClientException;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.uri.UriComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A common base class for all HTTP-based protocol clients.
 * 
 * @author Martynas Jusevičius <martynas@atomgraph.com>
 */
public abstract class ClientBase
{
    
    private static final Logger log = LoggerFactory.getLogger(ClientBase.class);

    private final WebTarget webTarget;
    private final MediaTypes mediaTypes;
    
    protected ClientBase(WebTarget webTarget, MediaTypes mediaTypes)
    {
        if (webTarget == null) throw new IllegalArgumentException("WebTarget cannot be null");
        if (mediaTypes == null) throw new IllegalArgumentException("MediaTypes cannot be null");

        this.webTarget = webTarget;
        this.mediaTypes = mediaTypes;
    }
    
    public abstract MediaType getDefaultMediaType();
    
    public ClientBase register(ClientRequestFilter filter)
    {
        if (filter == null) throw new IllegalArgumentException("ClientRequestFilter cannot be null");

        getWebTarget().register(filter);

        return this;
    }
    
    protected WebTarget applyParams(MultivaluedMap<String, String> params)
    {
        return applyParams(getWebTarget(), params);
    }
    
    protected WebTarget applyParams(WebTarget webTarget, MultivaluedMap<String, String> params)
    {
        if (params != null)
            for (Map.Entry<String, List<String>> entry : params.entrySet())
                for (String value : entry.getValue())
                    webTarget = webTarget.queryParam(UriComponent.encode(entry.getKey(), UriComponent.Type.UNRESERVED),
                        UriComponent.encode(value, UriComponent.Type.UNRESERVED));
        
        return webTarget;
    }
    
    public Response head(Class clazz, javax.ws.rs.core.MediaType[] acceptedTypes, String uri, MultivaluedMap<String, String> params, MultivaluedMap<String, String> headers)
    {
        if (log.isDebugEnabled()) log.debug("HEAD {}", getWebTarget().getUri(), uri);
        Invocation.Builder builder = applyParams(params).request(acceptedTypes);
        Response cr = builder.method("HEAD", Response.class);

        if (!cr.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL))
        {
            if (log.isErrorEnabled()) log.error("Request to Graph Store: {} unsuccessful. Reason: {}", getWebTarget().getUri(), cr.getStatusInfo().getReasonPhrase());
            throw new ClientException(cr);
        }

        return cr;
    }

    public Response get(javax.ws.rs.core.MediaType[] acceptedTypes, MultivaluedMap<String, String> params)
    {
        return get(applyParams(params).request().accept(acceptedTypes));
    }
    
    public Response get(Invocation.Builder builder)
    {
        if (log.isDebugEnabled()) log.debug("GET {}", getWebTarget().getUri());
        Response cr = builder.get(Response.class);

        if (!cr.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL))
        {
            if (log.isErrorEnabled()) log.error("GET {} request unsuccessful. Reason: {}", getWebTarget().getUri(), cr.getStatusInfo().getReasonPhrase());
            throw new ClientException(cr);
        }

        return cr;
    }
    
    public Response post(Object body, MediaType contentType, javax.ws.rs.core.MediaType[] acceptedTypes, MultivaluedMap<String, String> params)
    {
        return post(applyParams(params).request().accept(acceptedTypes), body, contentType);
    }
    
    public Response post(Invocation.Builder builder, Object body, MediaType contentType)
    {
        if (log.isDebugEnabled()) log.debug("POST {}", getWebTarget().getUri());
        Response cr = builder.post(Entity.entity(body, contentType)); // builder.post(Response.class, body);

        if (!cr.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL))
        {
            if (log.isErrorEnabled()) log.error("Request to {} unsuccessful. Reason: {}", getWebTarget().getUri(), cr.getStatusInfo().getReasonPhrase());
            throw new ClientException(cr);
        }
        
        return cr;
    }
    
    public Response put(Object body, MediaType contentType, javax.ws.rs.core.MediaType[] acceptedTypes, MultivaluedMap<String, String> params)
    {
        return put(applyParams(params).request().accept(acceptedTypes), body, contentType);
    }

    public Response put(Invocation.Builder builder, Object body, MediaType contentType)
    {
        if (log.isDebugEnabled()) log.debug("PUT {}", getWebTarget().getUri());
        Response cr = builder.put(Entity.entity(body, contentType)); // put(Response.class, body);

        if (!cr.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL))
        {
            if (log.isErrorEnabled()) log.error("PUT {} request unsuccessful. Reason: {}", getWebTarget().getUri(), cr.getStatusInfo().getReasonPhrase());
            throw new ClientException(cr);
        }

        return cr;
    }
    
    public Invocation.Builder deleteBuilder(javax.ws.rs.core.MediaType[] acceptedTypes, MultivaluedMap<String, String> params)
    {
        Invocation.Builder builder = applyParams(params).request();
        if (acceptedTypes != null) builder.accept(acceptedTypes);
        return builder;
    }

    public Response delete(javax.ws.rs.core.MediaType[] acceptedTypes, MultivaluedMap<String, String> params)
    {
        return delete(deleteBuilder(acceptedTypes, params));
    }

    public Response delete(Invocation.Builder builder)
    {
        if (log.isDebugEnabled()) log.debug("DELETE {}", getWebTarget().getUri());
        Response cr = builder.delete(Response.class);

        if (!cr.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL))
        {
            if (log.isErrorEnabled()) log.error("DELETE {} request unsuccessful. Reason: {}", getWebTarget().getUri(), cr.getStatusInfo().getReasonPhrase());
            throw new ClientException(cr);
        }

        return cr;
    }

    
    public MediaType[] getReadableMediaTypes(Class clazz)
    {
        return getMediaTypes().getReadable(clazz).toArray(new javax.ws.rs.core.MediaType[0]);
    }

    public final WebTarget getWebTarget()
    {
        return webTarget;
    }
    
    public MediaTypes getMediaTypes()
    {
        return mediaTypes;
    }
    
}
