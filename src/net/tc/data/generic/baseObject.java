package net.tc.data.generic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.beanutils.BeanUtils;
import java.util.Collection;
import net.tc.lang.LanguageSelector;
import java.beans.IntrospectionException;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.InputStream;
//import java.io.StringBufferInputStream;
import java.io.InputStreamReader;
import net.tc.utils.Text;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;


public abstract class baseObject extends dataObject
	implements Map
{
	//private Map internalValues = new HashMap();
	private Vector keys = new Vector(0,1);
	private Vector values = new Vector(0,1);
	public static Boolean isMap = null;

	public baseObject()
	{
	}

	public List getProperties() throws IntrospectionException
	{
		BeanInfo bi;
		PropertyDescriptor[] pd;
		bi = java.beans.Introspector.getBeanInfo( itemClass() );
		pd = bi.getPropertyDescriptors();
		ArrayList editableProperties = new ArrayList();

		for( int ipd = 0; ipd < pd.length; ipd++ )
		{
			String nameForRequest = pd[ipd].getName();
			String[] notToProcess = getExcludedProperty();
			boolean process = true;
			for( int intp = 0; intp < notToProcess.length; intp++ )
			{
				if( notToProcess[intp].equals( nameForRequest ) )
				{
					process = false;
					break;
				}
			}
			if( process )
			{
				editableProperties.add( nameForRequest );
			}

		}

		return editableProperties;
	}

	public Object getPropertyValue( Object obj, String propertyName )
	{
		Object value = null;
		try
		{
			value = BeanUtils.getProperty( obj, propertyName );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		return value;
	}
//MAP methods

	public int hashCode()
	{
		if( keys == null && values == null )
			return 0;

		return keys.hashCode();
	}

	public int size()
	{
		if( keys == null && values == null  )
			return 0;

		return keys.size();
	}

	public void clear()
	{
		if( keys == null && values == null)
			return;

		keys.clear();
		values.clear();
	}

	public boolean isEmpty()
	{
		if( keys == null && values == null  )
			return true;
		return keys.isEmpty();
	}

	public boolean containsKey( Object key )
	{
		if( keys == null )
			return false;

		return keys.contains( key );
	}

	public boolean containsValue( Object value )
	{
		if( values == null )
			return false;

		return values.contains( value );
	}

	public Collection values()
	{
		if( keys == null && values == null  )
			return null;

		return values;
	}

	public void putAll( Map t )
	{
		if( t == null )
			return;
		try{
			Iterator it = t.keySet().iterator();
			while( it.hasNext() )
			{
				Object tk = it.next();
				Object tv = t.get( tk );
				put( tk, tv );
			}
		}catch(Exception ex)
		{ex.printStackTrace();}

	}

	public Set entrySet()
	{
			return null;
	}

	public Set keySet()
	{
		return null;
	}

	public Object get( Object key )
	{
		if( key == null || keys == null || values == null)
			return null;

		if(keys.contains(key))
		{
			int index = keys.indexOf(key);
			return values.get(index);
		}
		return null;
	}

	public int getAsInt( Object key )
	{
		if( key == null || keys == null || values == null)
			return 0;

		try
		{
			if( get( key ) instanceof Integer )
				return( ( Integer ) get( key ) ).intValue();

			int i = Integer.parseInt( get( key ).toString() );
			return i;
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		return 0;

	}

	public String getAsString( String key, LanguageSelector ls )
	{
		return getAsString( ( ( String ) key ) + ls.getCurrentLanguage() );
	}

	public String getAsString( Object key )
	{
		if(  key == null || keys == null || values == null)
			return null;

		try
		{
			return get( key ).toString();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		return null;
	}

	public Object remove( Object key )
	{
		if(  key == null || keys == null || values == null )
			return null;
		try
		{
			if(keys.contains(key))
			{
				int index = keys.indexOf( key );
				keys.remove(index);
				values.remove(index);
				return null;
			}

			return null;
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		return null;
	}

	public Object  put( Object tk, Object tv )
	{
		if(  tk == null || keys == null || values == null )
			return null;

		keys.add( tk );
		int index = keys.indexOf( tk );
		values.add( index, tv );


		return null;
	}

	public Object getAsBean()
	{
		dataObject beanSource = null;

		try{beanSource = (dataObject)this.getClass().newInstance();}
		catch( Exception ex ){ex.printStackTrace();	}

		return getAsBean( beanSource, null );
	}
	public Object getAsBean(Map rowValues )
	{
		dataObject beanSource = null;
		try{beanSource =(dataObject) this.getClass().newInstance();}
		catch( Exception ex ){ex.printStackTrace();	}

		return getAsBean( beanSource, rowValues );
	}

	public Object getAsBean( dataObject beanSource, Map rowValues )
	{
		String beanClassName = beanSource.getClass().getName() + "Bean";
		Object bean = null;
		try{bean = Class.forName( beanClassName ).newInstance();}catch(Exception ex )
		{ex.printStackTrace();}

		if( beanSource == null && rowValues == null )
			return null;
		else if( beanSource != null && rowValues == null )
		{
			bean = getAsBeanXml( bean, rowValues );
			return bean;
		}

		try
		{
			Map RetrivableFields = beanSource.getRetrivableFieldsMap();
			if(RetrivableFields == null || RetrivableFields.size() == 0)
				return null;

			Iterator it = rowValues.keySet().iterator();
			while( it.hasNext() )
			{
				Object oKey = it.next();
				if( RetrivableFields.containsValue( oKey ) )
				{
					Object value = ( Object ) rowValues.get( oKey );

					if( value != null && !value.equals( "null" ) )
					{
						if( value instanceof String )
							( ( String ) value ).trim();
						BeanUtils.setProperty( bean, ( String ) oKey, value );
					}
					else
					{
						BeanUtils.setProperty( bean, ( String ) oKey, null );
					}

				}
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return bean;
	}

	public Object getAsBeanXml( Object bean, Map rowValues )
	{
		try
		{


//			List xmlProperties = new ArrayList(rowValues.keySet());
			String encoding = (String)get("ENCODING");
			if(encoding == null)
				return null;

			BeanInfo bi = java.beans.Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] pd = bi.getPropertyDescriptors();

			Iterator itProp = keySet().iterator();


			while(itProp.hasNext())
			{
				String s = (String)itProp.next();
				System.out.println(s + " = " + Text.getEncodedString(get(s), encoding));
//				if(get(s) != null)
//					fw.write(Text.getEncodedString(get(s), encoding));
			}
//			fw.close();
//			f = null;
//			for(int xp = 0 ; xp < xmlProperties.size(); xp++ )
//			{
//
//
//			}

//			while( it.hasNext() )
//			{
//				Object oKey = it.next();
//				if( RetrivableFields.containsValue( oKey ) )
//				{
//					Object value = ( Object ) rowValues.get( oKey );
//
//					if( value != null && !value.equals( "null" ) )
//					{
//						if( value instanceof String )
//							( ( String ) value ).trim();
//						BeanUtils.setProperty( bean, ( String ) oKey, value );
//					}
//					else
//					{
//						BeanUtils.setProperty( bean, ( String ) oKey, null );
//					}
//
//				}
//			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return bean;
	}

	public boolean hasMap( Class cls )
	{
		if( isMap != null )
			return isMap.booleanValue();

		try
		{

			List classes = Arrays.asList( cls.getClass().getInterfaces() );
			if( classes.contains( Map.class ) || ( this instanceof Map ) )
			{
				isMap = isMap.TRUE;
				return isMap.booleanValue();
			}

			if( cls.getSuperclass() != null )
				return hasMap( cls.getSuperclass() );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return false;
	}

	public abstract void init( Map rowValues );

	public abstract Class itemClass();

	public abstract String[] getExcludedProperty();

	public abstract List getRetrivableFields();
	public abstract Object getId();

 //*************************** COMMON Methods
  private void writeObject( ObjectOutputStream oos ) throws IOException
  {
	  oos.defaultWriteObject();
  }

 private void readObject( ObjectInputStream ois ) throws ClassNotFoundException, IOException
 {
	 ois.defaultReadObject();
 }


}
