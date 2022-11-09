package com.FlagHome.backend.v1.reply.controller;

import com.FlagHome.backend.v1.post.entity.Post;
import com.FlagHome.backend.v1.post.repository.PostRepository;
import com.FlagHome.backend.v1.reply.dto.ReplyDto;
import com.FlagHome.backend.v1.reply.entity.Reply;
import com.FlagHome.backend.v1.reply.repository.ReplyRepository;
import com.FlagHome.backend.v1.user.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@WithMockUser
class ReplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private User mockUser;
    @Mock
    private Post mockPost;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "/reply";

    @BeforeEach
    public void testSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    public void createReplyTest() throws Exception {
        ReplyDto replyDto = new ReplyDto();
        replyDto.setUserId(mockUser.getId());
        replyDto.setPostId(mockPost.getId());
        replyDto.setReplyGroup(1);
        replyDto.setReplyOrder(2);
        replyDto.setReplyDepth(3);
        replyDto.setContent("testReplyContent");
        String jsonBody = objectMapper.writeValueAsString(replyDto);

        mockMvc.perform(post(baseUrl + "/create")
                .with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", is("testReplyContent")))
                .andExpect(jsonPath("replyGroup", is(1)))
                .andExpect(jsonPath("replyOrder", is(2)))
                .andExpect(jsonPath("replyDepth", is(3)))
                .andDo(print());
    }

    @Test
    @DisplayName("PostID로 댓글 조회 테스트")
    public void findRepliesByPostIdTest() throws Exception {
        Post post = Post.builder().user(mockUser).title("제목이다").content("내용이다").build();
        Post savedPost = postRepository.save(post);

        for(int i = 0; i < 4; ++i) {
            Reply reply = Reply.builder().post(savedPost).user(mockUser).content(i + "번째").replyGroup(1L).replyOrder((long)i).replyDepth(1L).build();
            replyRepository.save(reply);
        }

        String postId = Long.toString(post.getId());
        MvcResult mvcResult = mockMvc.perform(get(baseUrl + "/get")
                .param("id", postId))
                .andExpect(status().isOk())
                .andReturn();

        List<ReplyDto> foundReplies = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<ReplyDto>>() {});
        for(long i = 0; i < 4; ++i) {
            ReplyDto replyDto = foundReplies.get((int)i);
            assert replyDto.getContent().equals(i + "번째");
            assert replyDto.getReplyOrder() == i;
        }
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    public void deleteReplyTest() throws Exception {
        Reply reply = Reply.builder().user(mockUser).post(mockPost).build();
        Reply savedReply = replyRepository.save(reply);
        long savedReplyId = savedReply.getId();

        mockMvc.perform(delete(baseUrl + "/delete/" + savedReplyId)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print());

        Reply isDelete = replyRepository.findById(savedReplyId).orElse(null);
        assert (isDelete == null);
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    public void updateReplyTest() throws Exception {
        final String originalContent = "원래내용";
        final String modifiedContent = "바뀐내용";

        Reply reply = replyRepository.save(Reply.builder().post(mockPost).user(mockUser).content(originalContent).build());
        assert reply.getContent().equals(originalContent);

        ReplyDto replyDto = new ReplyDto();
        replyDto.setId(reply.getId());
        replyDto.setContent(modifiedContent);
        String jsonBody = objectMapper.writeValueAsString(replyDto);

        mockMvc.perform(put(baseUrl + "/modify")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andDo(print());

        Reply modifiedReply = replyRepository.findById(replyDto.getId()).orElse(null);
        assert modifiedReply != null;
        assert modifiedReply.getContent().equals(modifiedContent);
    }
}