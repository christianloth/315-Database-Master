# NCAAQuery
#### Welcome to NCAAQuery!
This project is a tool to view and analyze Collegiate football data from 2005-2013. To get started, download the jar file (out/artifacts/team15_BDGUI/BDGUI.jar). This can be run using java -jar BDGUI.jar in the command line.

## How to Use:
#### Get data from one table:
Select a table from the drop-down in the upper left corner. Then, use the attribute menu below to select which columns you wish to view. To see all columns, leave the checkboxes all unchecked. Click generate to view the result.

#### Get data from multiple tables:
After selecting the first table, click the plus button in the upper right corner. Select the columns you wish to see from this table using the attribute menu below. 

#### Search for specific data:
To search for a specific value in a column, use the search bar below the "select attributes" menu. Type in the column name you want to look in followed by the value you are looking for. For example, if you want to search the firstname column of the player table for "John", you would type "firstname='John'" into the search bar. If you want to search by multiple attributes, separate them with commas. (ie. "firstname='John', lastname='Smith')

#### Output table data to a CSV file:
If you want to view the output as a CSV file, simply use the same approach to filing out the fields and click "Save to CSV".
