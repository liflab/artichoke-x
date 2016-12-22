/*
    Artichoke, enforcement of document lifecycles
    Copyright (C) 2016-2017 Sylvain Hallé
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.
    
    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.artichoke;

public class Action 
{	
	private final String m_name;
	
	public Action(String name)
	{
		super();
		m_name = name;
	}	
	
	public String getName()
	{
		return m_name;
	}
	
	@Override
	public String toString()
	{
		return m_name;
	}
}
