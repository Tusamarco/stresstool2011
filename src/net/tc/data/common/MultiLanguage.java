package net.tc.data.common;
import net.tc.lang.LanguageSelector;



public class MultiLanguage
{
	public MultiLanguage()
	{
	}
	///////// MULTI-LINGUAL ACCESSORS & MUTATORS //////////

	public String getName( LanguageSelector lang )
	{
		return( String ) lang.getMultilingualProperty( this, "name" );
	}
	public String getNameLong( LanguageSelector lang )
	{
            try{
                return (String) lang.getMultilingualProperty(this, "nameLong");
            }catch(Exception ex)
            {
                return  getNamelong( lang );
            }
	}
        public String getNamelong( LanguageSelector lang )
        {
                return( String ) lang.getMultilingualProperty( this, "namelong" );
        }
        public String getMultilingualProperty( LanguageSelector lang, String property )
        {
                return( String ) lang.getMultilingualProperty( this, property );
        }
        public void setMultilingualProperty( LanguageSelector lang, String property, Object value )
        {
                lang.setMultilingualProperty( this, property, value );
        }


///////// GENERATED ACCESSOR METHODS /////////

}
