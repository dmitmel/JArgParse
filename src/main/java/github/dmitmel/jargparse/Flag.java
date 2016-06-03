package github.dmitmel.jargparse;

public class Flag extends Argument {
    @Override
    public Type getType() {
        return Type.FLAG;
    }

    public Flag(String name, String helpInfo, String metaVar) {
        this(name, null, helpInfo, metaVar);
    }

    public Flag(String name, String longName, String helpInfo, String metaVar) {
        super.name = name;
        super.longName = longName;
        super.helpInfo = helpInfo;
        super.metaVar = metaVar;
    }

    public static boolean isStringArgumentFlag(String stringArg) {
        return stringArg.startsWith("-");
    }

    public static Flag findFromArgumentListByName(ArgumentList argumentList, String receivedName) {
        return (Flag) findFromArgumentListByName(argumentList, Type.FLAG, receivedName);
    }
}
