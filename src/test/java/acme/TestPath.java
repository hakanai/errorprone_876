package acme;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertThat;

import static acme.BlockMatchers.throwsException;

public class TestPath
{
    @Test
    public void testToRealPath_NonExistentFile() throws Exception
    {
        // real code uses TemporaryFolder here but not sure what's present
        Path file = Paths.get("non-existent.txt");
        assertThat(file::toRealPath,
                   throwsException(NoSuchFileException.class));
    }
}
