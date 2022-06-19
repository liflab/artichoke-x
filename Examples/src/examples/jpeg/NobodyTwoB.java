package examples.jpeg;

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.artichoke.Action;
import ca.uqac.lif.artichoke.Group;
import ca.uqac.lif.artichoke.Peer;
import ca.uqac.lif.artichoke.Policy;
import ca.uqac.lif.artichoke.PolicyViolationException;

public class NobodyTwoB implements Policy
{
	protected Map<String,Integer> m_counter;
	
	public NobodyTwoB()
	{
		super();
		m_counter = new HashMap<String,Integer>();
	}
	
	@Override
	public void reset() 
	{
		m_counter.clear();
	}

	@Override
	public boolean evaluate(Peer p, Action a, Group g) throws PolicyViolationException 
	{
		if (a.getName().compareTo("b") != 0)
		{
			return true;
		}
		int count = 0;
		if (m_counter.containsKey(p.getName()))
		{
			count = m_counter.get(p.getName());
		}
		if (count == 1)
		{
			throw new PolicyViolationException("Policy is violated: " + p.getName() + " has two 'b'");
		}
		m_counter.put(p.getName(), count + 1);
		return true;
	}
}
