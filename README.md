# Scalavatar

Simple avatar server similar to Gravatar. 

Goal of scalavatar is working as avatar server for GitHub Enterprise in on premise environment.

## Build & Run

First, you need to install gradle.
Then run commands

```sh
$ cd scalavatar
$ gradle packageAsStandalone
$ java -jar ./scalavatar.war
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
