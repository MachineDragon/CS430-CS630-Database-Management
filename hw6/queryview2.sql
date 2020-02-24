Select mgrname
FROM ManagerSummary
WHERE empcount>=ALL(SELECT empcount FROM ManagerSummary);
