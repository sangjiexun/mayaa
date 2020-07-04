package org.seasar.mayaa.regressions.issue13;

import java.io.IOException;

import org.junit.Test;
import org.seasar.mayaa.functional.EngineTestBase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class Issue13ReproductionTest extends EngineTestBase {

    @Test
    public void 独自プロセッサーでパラメータ型チェックで例外() throws IOException {
        // Given
        getServletContext().setAttribute("s0", "sv0");

        // When
        MockHttpServletRequest request = createRequest("/it-case/engine/issue13/a.html");
        MockHttpServletResponse response = exec(request, null);
        verifyResponse(response, "/it-case/engine/issue13/a-expected.html");


        // When
        request = createRequest("/it-case/engine/issue13/b.html");
        response = exec(request, null);

        // Then
        verifyResponse(response, "/it-case/engine/issue13/b-expected.html");
    }

}
