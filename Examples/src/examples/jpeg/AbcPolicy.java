package examples.jpeg;

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.artichoke.Action;
import ca.uqac.lif.artichoke.Group;
import ca.uqac.lif.artichoke.Peer;
import ca.uqac.lif.artichoke.Policy;
import ca.uqac.lif.artichoke.PolicyViolationException;

public class AbcPolicy implements Policy
{
	protected Map<String,String> m_next;
	
	public AbcPolicy()
	{
		super();
		m_next = new HashMap<String,String>();
	}
	
	@Override
	public void reset() 
	{
		m_next.clear();
	}

	@Override
	public boolean evaluate(Peer p, Action a, Group g) throws PolicyViolationException 
	{
		String next = "a";
		if (m_next.containsKey(g.getName()))
		{
			next = m_next.get(g.getName());
		}
		if (a.getName().compareTo(next) != 0)
		{
			throw new PolicyViolationException("Policy is violated");
		}
		m_next.put(g.getName(), nextAction(next));
		return true;
	}
	
	protected static String nextAction(String action)
	{
		switch (action)
		{
		case "a":
			return "b";
		case "b":
			return "c";
		default:
			return "a";
		}
	}

}
