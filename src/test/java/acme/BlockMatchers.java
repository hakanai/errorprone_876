package acme;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;

import com.google.common.base.Throwables;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

class BlockMatchers
{
    public static Matcher<ExceptionalRunnable> throwsException(Class<? extends Exception> exceptionClass)
    {
        return throwsException(Matchers.instanceOf(exceptionClass));
    }

    public static Matcher<ExceptionalRunnable> throwsException(Matcher<? extends Exception> exceptionMatcher)
    {
        return new TypeSafeMatcher<ExceptionalRunnable>()
        {
            private final Map<ExceptionalRunnable, Exception> actualExceptions = new WeakHashMap<>();

            @Override
            protected boolean matchesSafely(ExceptionalRunnable exceptionalRunnable)
            {
                try
                {
                    exceptionalRunnable.run();
                    actualExceptions.put(exceptionalRunnable, null);
                    return false;
                }
                catch (Exception e)
                {
                    actualExceptions.put(exceptionalRunnable, e);
                    return exceptionMatcher.matches(e);
                }
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendText("throws ");
                exceptionMatcher.describeTo(description);
            }

            @Override
            protected void describeMismatchSafely(ExceptionalRunnable exceptionalRunnable, Description mismatchDescription)
            {
                // Avoids calling the runnable again because it can produce a different result the second time. :(
                Exception actualException = actualExceptions.get(exceptionalRunnable);
                if (actualException == null)
                {
                    mismatchDescription.appendText("ran successfully (!!)");
                }
                else
                {
                    mismatchDescription.appendText("threw this instead: ")
                                       .appendText(Throwables.getStackTraceAsString(actualException));
                }
            }
        };
    }

    @FunctionalInterface
    public interface ExceptionalRunnable
    {
        void run() throws Exception;
    }
}