select F.flno, F.distance, F.arrives-F.departs 
from flights F 
where F.origin='Los-Angeles'AND F.destination='Honolulu';

select F.flno, F.distance, F.arrives-F.departs 
from flights F 
where F.origin='Los-Angeles'AND F.destination='Sydney';

select F.flno, F.distance, F.arrives-F.departs 
from flights F 
where F.origin='Madison'AND F.destination='Chicago';