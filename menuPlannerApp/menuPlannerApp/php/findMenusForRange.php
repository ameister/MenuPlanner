<?php
  mysql_connect("127.0.0.1","root","");
  mysql_select_db("MenuPlanner");
  $start = $_POST['start'];
  $end = $_POST['end'];
  $sql=mysql_query("SELECT d.id AS dayId, d.date AS dayDate, m.id AS menuId, m.name AS name
					FROM weekday d
					JOIN weekday_menu dm ON dm.Weekday_id = d.id
					JOIN menu m ON m.id = dm.menus_id
					WHERE date( d.date ) >= '$start'
					AND date( d.date ) <= '$end'");
  
  while($row=mysql_fetch_assoc($sql)) $output[]=$row;
  print(json_encode($output));
  mysql_close();
?>