package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;
    @Autowired
    CommentService commentService;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name = "start", required = false) String start,
                            @RequestParam(name = "end", required = false) String end,
                            HttpSession session) {
        ModelAndView mav = new ModelAndView();
        // セッションからエラーメッセージを取得
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            Integer sessionId = (Integer) session.getAttribute("sessionId");
            mav.addObject("errorMessage", errorMessage);
            mav.addObject("sessionId", sessionId);
            session.removeAttribute("errorMessage");
            session.removeAttribute("sessionId");
        }

        // 投稿を全件取得
        List<ReportForm> contentData = reportService.findAllReport(start, end);
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("contents", contentData);

        // commentForm用の空のentityを準備
        CommentForm commentForm = new CommentForm();
        // 準備した空のFormを保管
        mav.addObject("commentForm", commentForm);

        // コメント全権取得
        List<CommentForm> commentDate = commentService.findAllComment();
        // コメントデータオブジェクトを保管
        mav.addObject("comments", commentDate);
        return mav;

    }

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newContent(HttpSession session) {
        ModelAndView mav = new ModelAndView();
        // セッションからエラーメッセージを取得
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            mav.addObject("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") @Validated ReportForm reportForm,
                                   BindingResult result, HttpSession session) {
        // バリデーションの確認
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                String errorMessage = error.getDefaultMessage();
                session.setAttribute("errorMessage", errorMessage);
            }
            return new ModelAndView("redirect:/new");
        }
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id) {
        // 投稿をテーブルに格納
        reportService.deleteReport(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 編集画面表示処理
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        // セッションからエラーメッセージを取得
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            mav.addObject("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }
        // 編集する投稿を取得
        ReportForm report = reportService.editReport(id);
        // 編集する投稿をセット
        mav.addObject("formModel", report);
        // 画面遷移先を指定
        mav.setViewName("/edit");
        return mav;
    }

    /*
     * 編集処理
     */
    @PutMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id,
                                      @ModelAttribute("formModel") @Validated ReportForm report,
                                      BindingResult result, HttpSession session) {
        // バリデーションの確認
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                String errorMessage = error.getDefaultMessage();
                session.setAttribute("errorMessage", errorMessage);
            }
            return new ModelAndView("redirect:/edit/{id}");
        }

        // UrlParameterのidを更新するentityにセット
        report.setId(id);
        // 現在時刻をentityにセット
        report.setUpdatedDate(new Date());
        // 編集した投稿を更新
        reportService.saveReport(report);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント投稿処理
     */
    @PostMapping("/comment/{reportId}")
    public ModelAndView addComment(@PathVariable Integer reportId,
                                   @ModelAttribute("commentForm") @Validated CommentForm comment,
                                   BindingResult result, HttpSession session) {
        // バリデーションの確認
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                String errorMessage = error.getDefaultMessage();
                session.setAttribute("errorMessage", errorMessage);
            }
            session.setAttribute("sessionId", reportId);
            return new ModelAndView("redirect:/");
        }

        // UrlParameterのidを更新するentityにセット
        comment.setReportId(reportId);
        // 編集した投稿を更新
        commentService.saveComment(comment);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント編集画面表示処理
     */
    @GetMapping("/edit/comment/{id}")
    public ModelAndView editComment(@PathVariable Integer id, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        // セッションからエラーメッセージを取得
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            mav.addObject("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }
        // 編集する投稿を取得
        CommentForm comment = commentService.editComment(id);
        // 編集する投稿をセット
        mav.addObject("formModel", comment);
        // 画面遷移先を指定
        mav.setViewName("/edit_comment");
        return mav;
    }

    /*
     * コメント編集処理
     */
    @PutMapping("/update/comment/{id}")
    public ModelAndView updateComment(@PathVariable Integer id,
                                      @ModelAttribute("formModel") @Validated CommentForm comment, BindingResult result, HttpSession session) {
        // バリデーションの確認
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                String errorMessage = error.getDefaultMessage();
                session.setAttribute("errorMessage", errorMessage);
            }
            return new ModelAndView("redirect:/edit/comment/{id}");
        }

        // UrlParameterのidを更新するentityにセット
        comment.setId(id);
        // 編集した投稿を更新
        commentService.saveComment(comment);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/comment/{id}")
    public ModelAndView deleteComment(@PathVariable Integer id) {
        // 投稿をテーブルに格納
        commentService.deleteComment(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

}