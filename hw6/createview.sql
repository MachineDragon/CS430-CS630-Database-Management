CREATE VIEW ManagerSummary (DeptName, MgrID,MgrName, MgrSalary, EmpCount) 
AS 
SELECT D.dname, D.managerid, E.ename, E.salary, COUNT(W.eid)
FROM Dept D, Emp E, Works W
WHERE D.managerid=E.eid AND D.did=W.did
GROUP BY D.did, D.dname, D.managerid, E.ename, E.salary;
