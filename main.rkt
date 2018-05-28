#lang racket

(define (startsWith pref str)
  (let* ([len (string-length pref)]
         [strBegin (substring str 0 len)])
    (string=? pref strBegin)))

(define (fixRegisters line)
  (regexp-match* (regexp "[0-9]+") line))

(define (translateGate line)
  (let ([gate (list-ref (string-split line) 0)])
    (cond
      [(string=? gate "h") "H"]
      [(string=? gate "x") "X"]
      [(string=? gate "y") "Y"]
      [(string=? gate "z") "Z"]
      [(string=? gate "cx") "CNOT"]
      [(string=? gate "measure") "MEASURE"])))

(define (translateLine line)
  (let* ([regnums (fixRegisters line)]
         [gate (translateGate line)])
    (if (string=? gate "MEASURE")
        (string-append gate " " (list-ref regnums 0) " [" (list-ref regnums 1) "]")
        (string-append* gate " " (map (lambda (s) (string-append s " ")) regnums)))))

(define (translate filepath)
  (let* ([lines (list-tail (file->lines filepath) 4)]
         [translated (map translateLine lines)])
    (map (lambda (s) (printf (string-append s "\n"))) translated)))
    



    