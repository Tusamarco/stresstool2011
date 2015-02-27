package net.tc.isma.utils;

import java.io.*;
import java.util.*;
import net.tc.isma.resources.Resource;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Attribute;
import net.tc.isma.persister.IsmaPersister;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import net.tc.isma.IsmaException;
import org.xml.sax.InputSource;
import org.apache.xml.serialize.OutputFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.dom4j.io.DOMReader;
import net.tc.isma.xml.dom.StaticDocumentFactory;
import java.nio.charset.*;


public class IniFile extends Properties
	implements Resource
{
	public IniFile()
	{
		super();
	}

	public synchronized void load( String iniFile, boolean append, String mainKey ) throws IOException
	{
		load( new FileReader( iniFile ), append, mainKey  );
		File f = new File( iniFile );
		long lastMod = getProperty( "lastModify" ) != null ? Long.parseLong( this.getProperty( "lastModify" ) ) : 0;

		if( f.lastModified() > lastMod )
			setProperty( "lastModify", String.valueOf( f.lastModified() ) );

			setProperty( "iniFilePath", f.getPath().replace( '\\', '/' ) );
	}

	public synchronized void load( Reader rdr, boolean append, String  mainKey) throws IOException
	{
		BufferedReader in = new BufferedReader( rdr );
		if(mainKey.substring(mainKey.length()-1,mainKey.length()).equals("."))
			mainKey = mainKey.substring(0,mainKey.length()-1);
		String line;
		String section = "";

		if( !append )
		{
			clear();
		}

		while( ( line = in.readLine() ) != null )
		{
			// Trim tabs & spaces
			line = line.trim();

			// Skip blank lines and comments
			if( line.length() == 0 || line.startsWith( "#" ) )
			{
				continue;
			}

			if( line.startsWith( "[" ) && line.endsWith( "]" ) )
			{
				// Parse section heading
				section = line.substring( 1, line.length() - 1 ).trim().toLowerCase();
				if(section.indexOf(".") > 0)
				{
					String pre = section.substring(0,section.indexOf("."));
					String post = section.substring(section.indexOf("."));
					section = pre + "." + mainKey + post;
				}
				else
				{
					section = section + "." + mainKey;
				}

			}
			else if( line.indexOf( ":=" ) > 0 )
			{
				// Parse assignment
				int i = line.indexOf( ":=" );
				String name = line.substring( 0, i ).trim();
				String value = line.substring( i + 2 ).trim();

				if( section.length() == 0 )
				{
					setProperty( name.toLowerCase(), value );
				}
				else
				{
					setProperty( section + "." + name.toLowerCase(), value );
					IsmaPersister.getLogSystem().info("**** " + section + "." + name.toLowerCase() + " = " + value);
				}
			}
		}
	}

	public synchronized void loadXml( File file, boolean append ) throws IOException, IsmaException
	{

		if(file == null || !file.exists() || !file.canRead())
			throw new IsmaException("Bundle resource Not found = " + file.getPath());

		String encoding = null;
		Document document = null;
		Map docMap = StaticDocumentFactory.getDocument(file);
		if(docMap != null && docMap.size() <0)
		{
			if(docMap.get(Document.class) != null)
			{
				document = (Document)docMap.get(Document.class);
				encoding = (String)docMap.get("encoding");
				treeWalk( document.getRootElement(), encoding );
			}
		}
	}

	public synchronized void loadXmlRb( File file, boolean append, String mainKey ) throws IOException, IsmaException
	{
		if(file == null || !file.exists() || !file.canRead())
			throw new IsmaException("Bundle resource Not found = " + file.getPath());

		String encoding = null;
		Document document = null;
		Map docMap = StaticDocumentFactory.getDocument(file);
		if(docMap != null && docMap.size() > 0)
		{
			if(docMap.get(Document.class) != null)
			{
				document = (Document)docMap.get(Document.class);
				encoding = (String)docMap.get("encoding");
				treeWalkRb( document.getRootElement(), encoding, mainKey );
			}
		}

	}

	public synchronized void load( InputStream inStream ) throws IOException
	{
		/**@todo: Override this java.util.Properties method*/
		throw new UnsupportedOperationException( "Not yet implemented" );
//        load(new InputStreamReader(inStream), false);
	}


	public synchronized void store( OutputStream out, String header ) throws java.io.IOException
	{
		/**@todo: Override this java.util.Properties method*/
		throw new UnsupportedOperationException( "Not yet implemented" );
	}

	public static void main( String args[] )
	{
		try
		{
			String file = args[0];
			String mainKey = "";
			if(args[0].length() > 1)
				mainKey = args[1];
			IniFile ini = new IniFile();
			ini.load( file, false, mainKey );
			ini.list( System.out );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	public int getResourceType()
	{
		return 0;
	}

	public void load()
	{}

	public void loadXml()
	{}

	public void treeWalk( Element element, String encoding )
	{
		for( int i = 0; i < element.nodeCount(); i++ )
		{
			Node node = element.node( i );
			Map attributes = null;

			if( node instanceof Element )
			{
				String key = node.getPath().replaceAll( "/", "." ).replaceAll( "Text()", "" );
				if( key.startsWith( "." ) )
					key = key.substring( 1 );

				String lang = null;
				Object value = null;

				if( ( ( Element ) node ).attributes() != null )
				{
					attributes = getElementAttribute( ( Element ) node );
					if( attributes.containsKey( "xml:lang" ) || attributes.containsKey( "lang" ))
					{
						lang = (String)attributes.get("xml:lang");
						if(lang == null || lang.equals(""))
							lang = (String)attributes.get("lang");

						if(lang != null)
						{
							if(lang.toLowerCase().equals("cn"))
								lang = "zh";
							key = lang.toLowerCase() + "." + key;
						}
					}
				}
				value = node.getText();

				if( value instanceof String )
					value = Text.getEncodedString(cleanValue( ( String ) value ),encoding);

				if( !containsKey( key ) )
				{
					if( value != null )
					{
						setProperty(key, (String)value);
					}
				}
				treeWalk( ( Element ) node, encoding );
			}
			else
			{
//				System.out.println("********* " +node.getName());
//				System.out.println("********* " +node.getNodeType());
//				System.out.println("********* " +node.getNodeTypeName());
//				System.out.println("********* " +node.getPath());
//				System.out.println("********* " +node.getText());
			}
		}
		return;

	}
	public void treeWalk( Element element)
	{
		treeWalk( element, "UTF-8" );
	}
	public void treeWalkRb( Element element, String encoding, String mainKey )
	{
		try{
			for( int i = 0; i < element.nodeCount(); i++ )
			{
				Node node = element.node( i );
				Map attributes = null;

				if( node instanceof Element )
				{
//				String key = node.getPath().replaceAll( "/", "." ).replaceAll( "Text()", "" );
//				if( key.startsWith( "." ) )
//					key = key.substring( 1 );

					String keyname = null;
					if( ( ( Element ) node ).attributes() != null )
					{
						attributes = getElementAttribute( ( Element ) node );
						if( attributes.containsKey( "name" ) )
						{
							keyname = mainKey + ( String ) attributes.get( "name" );
							keyname = keyname.replace( ' ', '_' ).toLowerCase();
						}
					}

					Iterator valueNodes = ( ( Element ) node ).nodeIterator();
					while( valueNodes.hasNext() )
					{
						Object obj = valueNodes.next();
						if( obj instanceof Element )
						{
							String lang = null;
							Object value = null;
							String key = keyname;

							if( ( ( Element ) obj ).attributes() != null )
							{
								attributes = getElementAttribute( ( Element ) obj );
								if( attributes.containsKey( "xml:lang" ) || attributes.containsKey( "lang" ) )
								{
									lang = ( String ) attributes.get( "xml:lang" );
									if( lang == null || lang.equals( "" ) )
										lang = ( String ) attributes.get( "lang" );

									if(lang != null)
									{
										if(lang.toLowerCase().equals("cn"))
											lang = "zh";
										key = lang.toLowerCase() + "." + key;
									}

								}
							}

							value = ( ( Element ) obj ).getText();
							if( value instanceof String )
								value = cleanValue( ( String ) value );

							if( value != null
								&& !value.equals( "" )
								&& value instanceof String
								&& encoding != null
								&& !encoding.equals( "" ) )
							{
								try
								{
//									value = Text.getEncodedString( value, encoding );
								}
								catch( Exception ex )
								{
									ex.printStackTrace();
									continue;
								}
							}

							if( !containsKey( key ) )
							{
								if( value != null )
								{
//
									IsmaPersister.getLogSystem().info( "**** " + key + " = " + value );
									setProperty( key, ( String ) value );
									put( key + "o", value );
								}
							}

						}
					}
				}
				else
				{
//				System.out.println("********* " +node.getName());
//				System.out.println("********* " +node.getNodeType());
//				System.out.println("********* " +node.getNodeTypeName());
//				System.out.println("********* " +node.getPath());
//				System.out.println("********* " +node.getText());
				}
			}
		}catch(Exception ex){ex.printStackTrace();}
		return;

	}

	public static Map getElementAttribute( Element el )
	{
		Map am = null;
		if( el.attributes() != null )
		{
			am = new HashMap();
			List attributes = el.attributes();
			for( int ia = 0; ia < attributes.size(); ia++ )
			{
				Attribute a = ( Attribute ) attributes.get( ia );
				am.put( a.getQName().getName(), a.getValue() );
			}
		}

		return am;
	}
	public String cleanValue( String value )
	{
		if(value == null || value.equals(""))
			return null;

		String valueOut = value.replaceAll("\\t","").replaceAll("\\r","").replaceAll("\\n","");
		return valueOut;
	}

	public Object getResource()
	{
		return "";
	}

        public Map getValuesMap()
        {
            Map values = new SynchronizedMap(1);
            Enumeration keys = this.keys();

            while(keys.hasMoreElements())
            {
                Object k = keys.nextElement();
                values.put(k,this.get(k));

            }
            return values;

        }
}
