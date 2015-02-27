package net.tc.data.generic;

import net.tc.data.common.MultiLanguage;
import java.util.Map;

abstract public class dataObject extends MultiLanguage
{
	public dataObject()
	{
	}

	public abstract Map getRetrivableFieldsMap();

}
