package sm;
import java.util.Comparator;


public class SLAuxComparator implements Comparator<SLAux>
{
	    public int compare(SLAux x, SLAux y)
	    {
	        // Assume neither string is null. Real code should
	        // probably be more robust
	        if (x.score > y.score)
	        {
	            return -1;
	        }
	        if (x.score < y.score)
	        {
	            return 1;
	        }
	        return 0;
	    }
}
