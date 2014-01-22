<?php
  mysql_connect("127.0.0.1","root","");
  mysql_select_db("MenuPlanner");
  $menuId = $_POST['menuId'];
  $sql=mysql_query("SELECT cp.id as 'cp.id', cp.amount, c.id as 'c.id', c.name, cp.unit
					FROM menu m
					JOIN condimentpos cp
					ON cp.menu_id = m.id
					JOIN condiment c
					ON c.id = cp.condiment_id
					WHERE m.id = $menuId");
  while($row=mysql_fetch_assoc($sql)) $output[]=$row;
  print(json_encode($output));
  mysql_close();
?>