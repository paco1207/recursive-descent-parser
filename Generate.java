public class Generate extends AbstractGenerate {


    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        // Print error message with line number
        System.out.println("Parsing encountered error at " + token.lineNumber);
        System.out.println("Caused by: " + explanatoryMessage);
        // Throw Compilation Exception
        throw new CompilationException(explanatoryMessage);
    }


}
