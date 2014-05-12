;; @main
(main)

;; @extend
(defclass Object () (()))

;; @Fault
(defclass Fault () ((msg :initform "Fault")))

;; @SoftwareFault;@Fault
(defclass SoftwareFault (Fault) ())

;; @catch;@SoftwareFault
(defun libbun-catch (e)
  (cond ((eq (type-of e) 'Fault) e)
        ((or (eq (type-of e) 'division-by-zero) ; sbcl
             (eq (type-of e) 'system::simple-division-by-zero)) ; clisp
         (let ((e1 (make-instance 'SoftwareFault)))
           (setf (slot-value e1 'msg) "division-by-zero")
           e1))
        (t e)))
