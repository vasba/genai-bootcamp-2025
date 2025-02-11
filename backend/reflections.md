Backend was generated using VS Code Insiders with GitHub Copilot Agent and Claude Sonnet 3.5

The approach was to modify the Technical requirements for the backend provided by Andrew to fit the language I choosen
and then ask the agent to generate the code for me. However I had the Python backed copied since creation of repo and that
I suspect influenced the architecture of the backend, which might not be according to the technical document but we will
notice that later.

It is not trivial to start the backend from CLI in the beginning so I resorted to using IntelliJ IDEA to run the backend
since it has better support to discover Spring Boot applications and run them than VS Code Insiders

Agent did not add the hibernate dialects orm library despite it used it.
Do not give the agent an entire task to do but dived the big task in small and  build on previous finished tasks
Sometimes it was going in circles and reverted back fixes to again fix them because they showed up in compilation