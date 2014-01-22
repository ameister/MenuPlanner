<?php
  mysql_connect("127.0.0.1","root","");
  mysql_select_db("MenuPlanner");
  $date = $_POST['date'];
  $sql=mysql_query("SELECT m.id AS id, m.name AS name
					FROM weekday d
					JOIN weekday_menu dm ON dm.Weekday_id = d.id
					JOIN menu m ON m.id = dm.menus_id
					WHERE date( d.date ) = '$date'");
  
  while($row=mysql_fetch_assoc($sql)) $output[]=$row;
  print(json_encode($output));
  mysql_close();
?>