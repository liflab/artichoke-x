package examples.jpeg;

import ca.uqac.lif.artichoke.Action;
import ca.uqac.lif.artichoke.Group;
import ca.uqac.lif.artichoke.Peer;
import ca.uqac.lif.artichoke.Policy;
import ca.uqac.lif.artichoke.PolicyViolationException;

public class CarlNoA implements Policy
{
	public CarlNoA()
	{
		super();
	}

	@Override
	public void reset() 
	{
	}

	@Override
	public boolean evaluate(Peer p, Action a, Group g) throws PolicyViolationException 
	{
		if (p.getName().compareTo("Carl") != 0 || a.getName().compareTo("a") != 0)
		{
			return true;
		}
		throw new PolicyViolationException("Policy is violated: Carl has an 'a'");
	}
}
