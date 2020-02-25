# aplicacion-productos

Simple applicacion for product management.

### What can you do?

- User management (and permissions)

- Product management

- Review products

### Where are the data?

This app uses [Firebase](https://firebase.google.com/) (a cloud database)

Firebase organizes the database in `collections` and `documents`.

In a normal database `colections` are `tables` and `documents` are `entries`.

An example of a collection would be:

`classroom (collection)`

    |- `43085435 (document) -- The number represents student id`

        |- `name: Jhon Doe`

        |- `...`

    |- `...`



My app uses 3 collections:

- `users`:
  
  ![user document](https://imgur.com/k2E48hc.png)

- `products`:
  
  ![product document](https://imgur.com/6haPw0e.png)

- `opinions`:
  
  ![opinion document](https://imgur.com/TFrXvnA.png)



## Let's get into the code (Highlights)

- `UserType.java`
  
  ![UserType](https://imgur.com/0YQcTZ6.png)

- `FireManager.java`
  
  ![FireManager](https://imgur.com/BrR7dqv.png)

- In `AdapterProducts.java`
  
  ![OnClick](https://imgur.com/XVpWsLx.png)

- ImageConverter, Product filtering...

## Demo

...




