## Challenge Description

There will be three types of queries to the database

insert a planet with name *a* and entrance fee *k* into the database
increase the entrance fee for all planets between *a, b* by *k* 
return the entrance fee for a planet name *a* from the database

### Input Format

The first line contains *n* the number of queries to be made to the database.

The next *n* lines contain queries of the following 3 types

*1 a k*, insert a planet with name  and entrance fee  into the database
*2 a b k*, increase the entrance fee for all planets between  by 
*3 a*, return the entrance fee for a planet name  from the database

### Constraints

*n <= 100000*

*|a|,|b| <= 10*

*0 <= k <= 10^5*

As discussed in class, every node in the tree should have a value field. To avoid time-outs, all queries, including range updates (type 2 queries), should run in time .

### Output Format

For each query of type *3* print the entrance fee of the planet in a new line.

### Sample Input 0

```
7
1 earth 10
1 venus 30
1 jupiter 5
2 earth venus 10
3 earth
3 venus
3 jupiter
```

### Sample Output 0

```
20
40
15
```

### Sample Input 1

```
7
1 earth 10
1 venus 30
2 earth venus 10
1 jupiter 5
3 earth
3 venus
3 jupiter
```

### Sample Output 1

```
20
40
5
```

### Sample Input 2

```
8
1 earth 10
1 venus 30
1 jupiter 5
2 z a 10
3 earth
3 venus
3 jupiter
3 mars
```

### Sample Output 2

```
20
40
15
-1
```
