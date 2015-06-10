/**
 * <p>Title: MySQL StressTool</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Marco Tusa Copyright (c) 2012</p>
 * @author Marco Tusa
 * @version 1.0
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * <p>Company: MySQL</p>
 *
 */

package com.mysql.stresstool;


import java.util.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
/**
 * This class exist because the "normal" implementation of the Map interface has a serious BUG
 * It return as Hash code the sum of the hash code that the map itself contains that means that
 * it is possible to have more then one hashcode with the same value. The effect is that if you
 * load a map inside another map the new Map could  replace an existing object also if the keys
 * are different.
 *
 * More: this map implementation is using vectors internally that are automatically syncronized
 */
public class SynchronizedMap implements Map
{
	private Vector keys = null;
	private Vector values = null;

	public SynchronizedMap()
	{
		keys = new Vector(0,1);
		values = new Vector(0,1);

	}
	public SynchronizedMap(int initialCapacity)
   {
	   keys = new Vector(initialCapacity,1);
	   values = new Vector(initialCapacity,1);
   }
   public SynchronizedMap(int initialCapacity, int loadFactor)
	{
		keys = new Vector(initialCapacity,loadFactor);
		values = new Vector(initialCapacity,loadFactor);
	}
	public SynchronizedMap(Map m)
	{
		keys = new Vector(0,1);
		values = new Vector(0,1);
		putAll(m);
	}


//private Map internalValues = new HashMap();
public static Boolean isMap = null;


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
public boolean containsSubKey( Object value, int pos )
{
	if( keys == null || keys.size() <= 0)
		return false;
	if(keys.get(0) instanceof SynchronizedMap)
	{
		if(((SynchronizedMap)keys.get(0)).keys.size() >= pos)
		{
			for( int i = 0; i < keys.size(); i++ )
			{
				SynchronizedMap k = ( SynchronizedMap ) keys.get( i );
				for(int ik = 0 ; ik < k.size() ; ik++)
				{
					if( k.values.get( ik ) != null && k.values.get( ik ).equals( value ) )
						return true;
				}

			}
		}
	}

	return false;
}

public boolean containsValue( Object value )
{
	if( values == null )
		return false;

	return values.contains( value );
}
public boolean containsInternalValue( Object value )
{
    if( values == null )
        return false;

    for(int i =0 ; i < values.size(); i++)
    {
        if(value instanceof SynchronizedMap)
        {
            if(((SynchronizedMap)values.get(i)).values.equals((SynchronizedMap)value))
                return true;
        }
        else
        {
            if(values.get(i).equals(value))
                return true;
        }
    }
    return false;
}
public boolean containsInternalKey( Object key )
{
    if( keys == null )
        return false;

    for(int i =0 ; i < values.size(); i++)
    {
        if(key instanceof SynchronizedMap)
        {
            if(((SynchronizedMap)keys.get(i)).values.equals(((SynchronizedMap)key).values))
                return true;
        }
        else
        {
            if(keys.get(i).equals(key))
                return true;
        }
    }
    return false;
}
public Object getInternalKey( Object key )
{
    if( keys == null )
        return null;

    for(int i =0 ; i < values.size(); i++)
    {
        if(key instanceof SynchronizedMap)
        {
            if(((SynchronizedMap)keys.get(i)).values.equals(((SynchronizedMap)key).values))
                return values.get(i);
        }
        else
        {
            if(keys.get(i).equals(key))
                return values.get(i);
        }
    }
    return null;
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
	{
            ex.printStackTrace();
        }

}
public Iterator iterator()
{
	if(keys == null)
		return null;

	return keys.iterator();
}
public Set keySet()
{
	if(keys != null)
		return new HashSet((Collection)keys.subList(0,keys.size()));
	return null;
}


public Set entrySet()
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

public List get(Object inkey, int pos)
{
	List  l = null;
	if( keys == null || keys.size() <= 0)
		return null;
	if(keys.get(0) instanceof SynchronizedMap)
	{
		if(((SynchronizedMap)keys.get(0)).keys.size() >= pos)
		{
			l = new ArrayList(0);
			for( int i = 0; i < keys.size(); i++ )
			{
				SynchronizedMap k = ( SynchronizedMap ) keys.get( i );
				if(k.size() >= pos)
				{
					if(k.values.get(pos) != null && k.values.get(pos).equals(inkey))
					{
						Object o = values.get(i);
						l.add(o);
					}

				}
			}
		}
	}
	return l;
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
/**
 * Insert an object into the collection
 */
public Object  put( Object tk, Object tv )
{
	if(  tk == null || keys == null || values == null )
		return null;

	if(keys.contains(tk))
		remove(tk);
	keys.add( tk );
	int index = keys.indexOf( tk );
	values.add( index, tv );


	return new Integer(index);
}



//*************************** COMMON Methods
private void writeObject( ObjectOutputStream oos ) throws IOException
{
  oos.defaultWriteObject();
}

private void readObject( ObjectInputStream ois ) throws ClassNotFoundException, IOException
{
 ois.defaultReadObject();
}


public Object[] getKeyasOrderedArray()
    {
        if(keys == null)
            return null;

        Object[] oA = new Object[keys.size()];
        for(int i = 0 ; i < keys.size() ; i++)
        {
            oA[i] = keys.get(i);
        }
        return oA;
    }

}
