package net.tc.utils.xmlxsl ;

import org.w3c.dom.Document ;
import java.io.StringWriter ;
import java.io.PrintWriter ;
import javax.xml.transform.stream.StreamResult ;
import javax.xml.transform.Templates ;
import javax.xml.transform.Result ;
import javax.xml.transform.Transformer ;
import javax.xml.transform.dom.DOMSource ;
import java.io.File ;
import javax.xml.transform.TransformerFactory ;
import java.io.InputStream ;
import javax.xml.transform.stream.StreamSource ;
import org.dom4j.io.DOMWriter;
import net.tc.isma.persister.IsmaPersister;
import java.util.Map;
import net.tc.isma.utils.SynchronizedMap;
import net.tc.isma.resources.ConfigResource;
import net.tc.isma.persister.persistentObjectImpl;
import net.tc.isma.xml.dom.StaticDocumentFactory;
import java.io.Serializable;
import net.tc.isma.persister.PersistentObject;
import net.tc.isma.data.objects.area;
import java.io.Reader;
import net.tc.isma.resources.Resource;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class XmlXslUtils
{


    public XmlXslUtils()
    {
    }
    public static String transformInputReaderToString(String xslTemplate, Reader strmXml, boolean reload)
    {
        Templates templates = getTemplatesFromPersister(xslTemplate);
        if(templates == null || reload)
        {
            templates = setTemplateInPersister(xslTemplate, templates);
        }



        StringWriter sw = new StringWriter() ;
        PrintWriter pw = new PrintWriter( sw ) ;
        internalTransform( strmXml , templates ,new StreamResult( pw ) , false ) ;
        return sw.toString() ;

    }
    public static String transformInputReaderToStringWithParams(String xslTemplate, Reader strmXml, String[] params, boolean reload)
    {
        Templates templates = getTemplatesFromPersister(xslTemplate);
        if(templates == null || reload)
        {
            templates = setTemplateInPersister(xslTemplate, templates);
        }



        StringWriter sw = new StringWriter() ;
        PrintWriter pw = new PrintWriter( sw ) ;
        internalTransformWithParams( strmXml , templates ,new StreamResult( pw ) ,false, params ) ;
        return sw.toString() ;

    }

    public static String transformInputStreamToString(String xslTemplate, InputStream strmXml, boolean reload)
    {
        Templates templates = getTemplatesFromPersister(xslTemplate);
        if(templates == null || reload)
        {
            templates = setTemplateInPersister(xslTemplate, templates);
        }



        StringWriter sw = new StringWriter() ;
        PrintWriter pw = new PrintWriter( sw ) ;
        internalTransform( strmXml , templates ,new StreamResult( pw ) , false ) ;
        return sw.toString() ;

    }
    public static String transformInputStreamToString( Templates xslTemplate, InputStream strmXml)
    {
        if(xslTemplate == null)
            return null;


        StringWriter sw = new StringWriter() ;
        PrintWriter pw = new PrintWriter( sw ) ;
        internalTransform( strmXml , xslTemplate ,new StreamResult( pw ) , false ) ;
        return sw.toString() ;

    }



    public static String transformToString( Templates xslTemplate , Document xml )
    {
        if(xslTemplate == null)
            return null;

        StringWriter sw = new StringWriter() ;
        PrintWriter pw = new PrintWriter( sw ) ;
        internalTransform( xml , xslTemplate ,new StreamResult( pw ) , false ) ;
        return sw.toString() ;
    }

    public static String transformToString( String xslTemplate , Document xml )
    {
        /** @todo REMOVE THE FORCED NULL  AND REENABLE THE getTEMPLATE FROM CACHE*/
        Templates templates = null;//getTemplatesFromPersister(xslTemplate);
        if(templates == null)
        {
            templates = setTemplateInPersister(xslTemplate, templates);
        }
        if(templates == null)
            return null;

        StringWriter sw = new StringWriter() ;
        PrintWriter pw = new PrintWriter( sw ) ;
        internalTransform( xml , templates ,new StreamResult( pw ) , false ) ;
        return sw.toString() ;
    }

    private static Templates setTemplateInPersister( String xslTemplate ,Templates templates )
    {
        int lifecycle = 0 ;

        ConfigResource param1 = ( ConfigResource ) IsmaPersister.getConfigResource( xslTemplate ) ;
        if (param1 != null &&  (param1.getStringValue() == null ||param1.getStringValue().equals( "" ) ))
            return null ;

        File f = null;
        if(param1 != null )
            f = new File( IsmaPersister.getMAINROOT() +param1.getStringValue() );
        else if (param1 == null)
            f = new File( xslTemplate );

        if ( !f.exists() )
            return null ;

        templates = getTemplatesByName( f );

        if ( param1 != null && param1.getParameter() != null && param1.getParameter().containsKey( "lifecycle" ) && param1.getParameter().get( "lifecycle" ) instanceof String )
            lifecycle = Integer.parseInt( ( String ) param1.getParameter().get("lifecycle" ) ) ;
        else
            lifecycle = Resource.PERSISTENT;

        PersistentObject poIn = new persistentObjectImpl( lifecycle , templates ) ;
        poIn.setKey( xslTemplate) ;
        IsmaPersister.set( ( Serializable ) poIn.getKey() , poIn ) ;

        return templates ;
    }

    protected static Result internalTransform( Document doc ,
                                               Templates templates , Result r ,
                                               boolean trace )
    {
        StringWriter sw = new StringWriter() ;

        try
        {
            Transformer transformer = templates.newTransformer() ;

            transformer.transform( new DOMSource( doc ) , r ) ;

            sw.close() ;
            return r ;
        }
        catch ( Throwable th )
        {
            th.printStackTrace();
            return r;
        }
    }
    protected static Result internalTransform( InputStream doc ,
                                               Templates templates , Result r ,
                                               boolean trace )
    {
        StringWriter sw = new StringWriter() ;

        try
        {
            Transformer transformer = templates.newTransformer() ;

            transformer.transform( new StreamSource(doc) , r ) ;

            sw.close() ;
            return r ;
        }
        catch ( Throwable th )
        {
            th.printStackTrace();
            return r;
        }

    }
    protected static Result internalTransform( Reader doc ,
                                               Templates templates , Result r ,
                                               boolean trace )
    {
        StringWriter sw = new StringWriter() ;

        try
        {
            Transformer transformer = templates.newTransformer() ;

            transformer.transform( new StreamSource(doc) , r ) ;

            sw.close() ;
            return r ;
        }
        catch ( Throwable th )
        {
            th.printStackTrace();
            return r;
        }

    }
    protected static Result internalTransformWithParams( Reader doc ,
                                               Templates templates , Result r ,
                                               boolean trace,
                                              String[] params
                                               )
    {
        StringWriter sw = new StringWriter() ;

        try
        {
            Transformer transformer = templates.newTransformer() ;
            if(params != null && params.length > 0)
            {
                for(int i=0 ; i < params.length ; i++)
                     transformer.setParameter("param_"+i, params[i]);
            }

            transformer.transform( new StreamSource(doc) , r ) ;

            sw.close() ;
            return r ;
        }
        catch ( Throwable th )
        {
            th.printStackTrace();
            return r;
        }

    }


    public static Templates getTemplatesByName( File xslF )
    {

        Templates templates = null ;
        TransformerFactory tfactory = TransformerFactory.newInstance() ;
        tfactory.setAttribute("http://xml.apache.org/xalan/features/incremental" ,java.lang.Boolean.TRUE ) ;

        InputStream is = null ;
        try
        {
            StreamSource ss = new StreamSource( xslF ) ;
            is = ss.getInputStream() ;
            templates = tfactory.newTemplates( ss ) ;
            if ( is != null )
            {
                is.close() ;
                is = null ;
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if ( is != null )
                {
                    is.close() ;
                    is = null ;
                }
            }
            catch ( Throwable t )
            {
                System.out.println( t ) ;
            }
        }

        return templates ;
    }

    public static org.w3c.dom.Document transformtoDOM( org.dom4j.Document dom )
    {
        try{
            DOMWriter writer = new DOMWriter() ;
            return writer.write( dom ) ;
        }catch(Exception ex)
        {IsmaPersister.getLogXmlXslTransformation().error(ex);}
        return null;
    }
    private static Templates getTemplatesFromPersister(String name)
    {
        if(name == null || name.equals(""))
            return null;

        return (Templates)IsmaPersister.get( org.apache.xalan.templates.StylesheetRoot.class, name);

    }

}
